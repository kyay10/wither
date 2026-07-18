package io.github.kyay10.wither.fir

import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.SessionAndScopeSessionHolder
import org.jetbrains.kotlin.fir.declarations.FirDeclarationDataKey
import org.jetbrains.kotlin.fir.declarations.FirDeclarationDataRegistry
import org.jetbrains.kotlin.fir.declarations.FirReceiverParameter
import org.jetbrains.kotlin.fir.declarations.FirResolvePhase
import org.jetbrains.kotlin.fir.declarations.FirValueParameter
import org.jetbrains.kotlin.fir.declarations.FirValueParameterKind
import org.jetbrains.kotlin.fir.declarations.builder.buildReceiverParameter
import org.jetbrains.kotlin.fir.declarations.builder.buildValueParameter
import org.jetbrains.kotlin.fir.declarations.origin
import org.jetbrains.kotlin.fir.expressions.FirFunctionCall
import org.jetbrains.kotlin.fir.expressions.FirVarargArgumentsExpression
import org.jetbrains.kotlin.fir.expressions.argument
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

data object FakeLegacyReceiverKey : FirDeclarationDataKey()

val FirValueParameterSymbol.fakeLegacyReceiver: FirReceiverParameter? by
  FirDeclarationDataRegistry.symbolAccessor(FakeLegacyReceiverKey)
var FirValueParameter.fakeLegacyReceiver: FirReceiverParameter? by
  FirDeclarationDataRegistry.data(FakeLegacyReceiverKey)

class WitherReceiverGenerator(session: FirSession) : FirExpressionResolutionExtension(session) {
  @OptIn(PrivateForInline::class)
  override fun addNewImplicitReceivers(
    functionCall: FirFunctionCall,
    sessionHolder: SessionAndScopeSessionHolder,
    containingCallableSymbol: FirBasedSymbol<*>,
  ): List<ImplicitExtensionReceiverValue> {
    if (functionCall.calleeReference.resolved?.resolvedSymbol == withSymbol) {
      val vararg = functionCall.argument as? FirVarargArgumentsExpression ?: return emptyList()
      val fakeValueParam = buildValueParameter {
        resolvePhase = FirResolvePhase.BODY_RESOLVE
        moduleData = session.moduleData
        origin = GeneratedReceiverFromWithKey.origin
        symbol = FirValueParameterSymbol()
        containingDeclarationSymbol = withSymbol
        returnTypeRef = session.builtinTypes.anyType
        name = Name.special("<with receiver holder>")
      }
      val contextParameters =
        vararg.arguments.map {
          val type = it.resolvedType
          val receiverParameter = buildReceiverParameter {
            resolvePhase = FirResolvePhase.BODY_RESOLVE
            moduleData = session.moduleData
            origin = GeneratedReceiverFromWithKey.origin
            symbol = FirReceiverParameterSymbol()
            containingDeclarationSymbol = fakeValueParam.symbol
            typeRef = buildResolvedTypeRef {
              coneType = type
            }
          }
          buildValueParameter {
            resolvePhase = FirResolvePhase.BODY_RESOLVE
            moduleData = session.moduleData
            origin = GeneratedReceiverFromWithKey.origin
            symbol = FirValueParameterSymbol()
            name = Name.special("<with receiver itself>")
            containingDeclarationSymbol = fakeValueParam.symbol
            returnTypeRef = buildResolvedTypeRef { coneType = type }
            valueParameterKind = FirValueParameterKind.ContextParameter
          }
            .apply { fakeLegacyReceiver = receiverParameter }
        }
      sessionHolder as FirAbstractBodyResolveTransformer.BodyResolveTransformerComponents
      val bodyResolveContext = sessionHolder.context
      bodyResolveContext.replaceTowerDataContext(
        bodyResolveContext.towerDataContext.addContextGroups(
          contextParameters.map {
            ImplicitContextParameterValue(
              boundSymbol = it.symbol,
              type = it.returnTypeRef.coneType,
            )
          }
        )
      )
    }
    return emptyList()
  }

  data object GeneratedReceiverFromWithKey : GeneratedDeclarationKey()

  private val withSymbol by lazy {
    session.symbolProvider
      .getTopLevelCallableSymbols(WITH_CALLABLE_ID.packageName, WITH_CALLABLE_ID.callableName)
      .firstIsInstance<FirFunctionSymbol<*>>()
  }
}
