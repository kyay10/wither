package io.github.kyay10.wither.fir

import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.SessionAndScopeSessionHolder
import org.jetbrains.kotlin.fir.declarations.FirResolvePhase
import org.jetbrains.kotlin.fir.declarations.builder.buildReceiverParameter
import org.jetbrains.kotlin.fir.declarations.builder.buildValueParameter
import org.jetbrains.kotlin.fir.declarations.origin
import org.jetbrains.kotlin.fir.expressions.FirFunctionCall
import org.jetbrains.kotlin.fir.expressions.FirVarargArgumentsExpression
import org.jetbrains.kotlin.fir.expressions.argument
import org.jetbrains.kotlin.fir.extensions.FirExpressionResolutionExtension
import org.jetbrains.kotlin.fir.extensions.captureValueInAnalyze
import org.jetbrains.kotlin.fir.moduleData
import org.jetbrains.kotlin.fir.references.resolved
import org.jetbrains.kotlin.fir.resolve.calls.ImplicitExtensionReceiverValue
import org.jetbrains.kotlin.fir.resolve.providers.symbolProvider
import org.jetbrains.kotlin.fir.symbols.impl.FirCallableSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirFunctionSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirReceiverParameterSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirValueParameterSymbol
import org.jetbrains.kotlin.fir.types.builder.buildResolvedTypeRef
import org.jetbrains.kotlin.fir.types.resolvedType
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.utils.addToStdlib.firstIsInstance

private val PACKAGE_FQNAME = FqName("io.github.kyay10.wither")
val WITH_CALLABLE_ID = CallableId(PACKAGE_FQNAME, Name.identifier("with"))

class WitherReceiverGenerator(session: FirSession) : FirExpressionResolutionExtension(session) {
  override fun addNewImplicitReceivers(
    functionCall: FirFunctionCall,
    sessionHolder: SessionAndScopeSessionHolder,
    containingCallableSymbol: FirCallableSymbol<*>
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
      return vararg.arguments.map {
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
        receiverParameter.captureValueInAnalyze = true
        ImplicitExtensionReceiverValue(
          receiverParameter.symbol,
          type,
          sessionHolder.session,
          sessionHolder.scopeSession
        )
      }
    }
    return emptyList()
  }

  data object GeneratedReceiverFromWithKey : GeneratedDeclarationKey()

  private val withSymbol by lazy {
    session.symbolProvider.getTopLevelCallableSymbols(WITH_CALLABLE_ID.packageName, WITH_CALLABLE_ID.callableName)
      .firstIsInstance<FirFunctionSymbol<*>>()
  }
}