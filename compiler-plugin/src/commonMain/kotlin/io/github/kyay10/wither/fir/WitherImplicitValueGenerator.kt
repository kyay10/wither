package io.github.kyay10.wither.fir

import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.descriptors.EffectiveVisibility
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.SessionAndScopeSessionHolder
import org.jetbrains.kotlin.fir.declarations.FirDeclarationDataKey
import org.jetbrains.kotlin.fir.declarations.FirDeclarationDataRegistry
import org.jetbrains.kotlin.fir.declarations.FirReceiverParameter
import org.jetbrains.kotlin.fir.declarations.FirResolvePhase
import org.jetbrains.kotlin.fir.declarations.FirValueParameter
import org.jetbrains.kotlin.fir.declarations.FirValueParameterKind
import org.jetbrains.kotlin.fir.declarations.builder.buildProperty
import org.jetbrains.kotlin.fir.declarations.builder.buildReceiverParameter
import org.jetbrains.kotlin.fir.declarations.builder.buildValueParameter
import org.jetbrains.kotlin.fir.declarations.impl.FirResolvedDeclarationStatusImpl
import org.jetbrains.kotlin.fir.declarations.origin
import org.jetbrains.kotlin.fir.expressions.FirFunctionCall
import org.jetbrains.kotlin.fir.expressions.dynamicVarargArguments
import org.jetbrains.kotlin.fir.extensions.FirExpressionResolutionExtension
import org.jetbrains.kotlin.fir.moduleData
import org.jetbrains.kotlin.fir.references.resolved
import org.jetbrains.kotlin.fir.resolve.calls.ImplicitContextParameterValue
import org.jetbrains.kotlin.fir.resolve.calls.ImplicitExtensionReceiverValue
import org.jetbrains.kotlin.fir.resolve.providers.symbolProvider
import org.jetbrains.kotlin.fir.resolve.transformers.body.resolve.FirAbstractBodyResolveTransformer
import org.jetbrains.kotlin.fir.symbols.FirBasedSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirFunctionSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirReceiverParameterSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirRegularPropertySymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirValueParameterSymbol
import org.jetbrains.kotlin.fir.types.builder.buildResolvedTypeRef
import org.jetbrains.kotlin.fir.types.coneType
import org.jetbrains.kotlin.fir.types.resolvedType
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.util.PrivateForInline
import org.jetbrains.kotlin.utils.addToStdlib.firstIsInstance

private val PACKAGE_FQNAME = FqName("io.github.kyay10.wither")
val WITH_CALLABLE_ID = CallableId(PACKAGE_FQNAME, Name.identifier("with"))
val CONTEXT_CALLABLE_ID = CallableId(PACKAGE_FQNAME, Name.identifier("context"))

val CALLABLE_IDS = setOf(WITH_CALLABLE_ID, CONTEXT_CALLABLE_ID)
val CALLABLE_NAMES = CALLABLE_IDS.map { it.callableName }.toSet()

val HOLDER_NAMES = CALLABLE_NAMES.associateWith { Name.special("<$it holder>") }

data object FakeLegacyReceiverKey : FirDeclarationDataKey()

val FirValueParameterSymbol.fakeLegacyReceiver: FirReceiverParameter? by
  FirDeclarationDataRegistry.symbolAccessor(FakeLegacyReceiverKey)
var FirValueParameter.fakeLegacyReceiver: FirReceiverParameter? by
  FirDeclarationDataRegistry.data(FakeLegacyReceiverKey)

class WitherImplicitValueGenerator(session: FirSession) :
  FirExpressionResolutionExtension(session) {
  @OptIn(PrivateForInline::class)
  override fun addNewImplicitReceivers(
    functionCall: FirFunctionCall,
    sessionHolder: SessionAndScopeSessionHolder,
    containingCallableSymbol: FirBasedSymbol<*>,
  ): List<ImplicitExtensionReceiverValue> {
    val reference = functionCall.calleeReference.resolved ?: return emptyList()
    val calleeSymbol = reference.resolvedSymbol
    if (calleeSymbol in symbols) {
      val vararg = functionCall.dynamicVarargArguments ?: return emptyList()
      val fakeValueParam = buildProperty {
        resolvePhase = FirResolvePhase.BODY_RESOLVE
        moduleData = session.moduleData
        origin = GeneratedReceiverFromWithKey.origin
        returnTypeRef = session.builtinTypes.anyType
        name =
          HOLDER_NAMES.getValue(
            if (calleeSymbol == contextSymbol) CONTEXT_CALLABLE_ID.callableName
            else WITH_CALLABLE_ID.callableName
          )
        symbol = FirRegularPropertySymbol(CallableId(CallableId.PACKAGE_FQ_NAME_FOR_LOCAL, name))
        status =
          FirResolvedDeclarationStatusImpl(
            Visibilities.DEFAULT_VISIBILITY,
            Modality.FINAL,
            EffectiveVisibility.Public,
          )
        isLocal = true
        isVar = false
        receiverParameter = buildReceiverParameter {
          resolvePhase = FirResolvePhase.BODY_RESOLVE
          moduleData = session.moduleData
          origin = GeneratedReceiverFromWithKey.origin
          symbol = FirReceiverParameterSymbol()
          containingDeclarationSymbol = this@buildProperty.symbol
          typeRef = buildResolvedTypeRef {
            coneType = session.builtinTypes.anyType.coneType
          }
        }
      }
      val receiverParams = vararg.map {
        val type = it.resolvedType
        buildReceiverParameter {
          resolvePhase = FirResolvePhase.BODY_RESOLVE
          moduleData = session.moduleData
          origin = GeneratedReceiverFromWithKey.origin
          symbol = FirReceiverParameterSymbol()
          containingDeclarationSymbol = fakeValueParam.symbol
          typeRef = buildResolvedTypeRef {
            coneType = type
          }
          source = it.source
        }
      }
      if (calleeSymbol == contextSymbol) {
        if (sessionHolder !is FirAbstractBodyResolveTransformer.BodyResolveTransformerComponents)
          return emptyList()
        val bodyResolveContext = sessionHolder.context
        bodyResolveContext.replaceTowerDataContext(
          bodyResolveContext.towerDataContext.addContextGroups(
            receiverParams.map {
              val valueParam = buildValueParameter {
                resolvePhase = FirResolvePhase.BODY_RESOLVE
                moduleData = session.moduleData
                origin = GeneratedReceiverFromWithKey.origin
                symbol = FirValueParameterSymbol()
                source = it.source
                name = Name.special("<context parameter itself>")
                containingDeclarationSymbol = fakeValueParam.symbol
                returnTypeRef = buildResolvedTypeRef { coneType = it.typeRef.coneType }
                valueParameterKind = FirValueParameterKind.ContextParameter
              }
                .apply { fakeLegacyReceiver = it }
              ImplicitContextParameterValue(
                boundSymbol = valueParam.symbol,
                type = it.typeRef.coneType,
              )
            }
          )
        )
      } else {
        return receiverParams.map {
          ImplicitExtensionReceiverValue(
            it.symbol,
            it.typeRef.coneType,
            sessionHolder.session,
            sessionHolder.scopeSession,
          )
        }
      }
    }
    return emptyList()
  }

  data object GeneratedReceiverFromWithKey : GeneratedDeclarationKey()

  private val withSymbol by lazy {
    session.symbolProvider
      .getTopLevelCallableSymbols(WITH_CALLABLE_ID.packageName, WITH_CALLABLE_ID.callableName)
      .firstIsInstance<FirFunctionSymbol<*>>()
  }
  private val contextSymbol by lazy {
    session.symbolProvider
      .getTopLevelCallableSymbols(CONTEXT_CALLABLE_ID.packageName, CONTEXT_CALLABLE_ID.callableName)
      .firstIsInstance<FirFunctionSymbol<*>>()
  }
  private val symbols by lazy { setOf(withSymbol, contextSymbol) }
}
