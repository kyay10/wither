package io.github.kyay10.wither.fir

import org.jetbrains.kotlin.KtSourceElement
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.expressions.FirAnonymousFunctionExpression
import org.jetbrains.kotlin.fir.expressions.FirExpression
import org.jetbrains.kotlin.fir.expressions.FirFunctionCall
import org.jetbrains.kotlin.fir.expressions.FirNamedArgumentExpression
import org.jetbrains.kotlin.fir.expressions.FirStatement
import org.jetbrains.kotlin.fir.expressions.FirVariableAssignment
import org.jetbrains.kotlin.fir.expressions.builder.buildArgumentList
import org.jetbrains.kotlin.fir.expressions.builder.buildBlock
import org.jetbrains.kotlin.fir.expressions.builder.buildFunctionCall
import org.jetbrains.kotlin.fir.expressions.builder.buildLiteralExpression
import org.jetbrains.kotlin.fir.expressions.builder.buildPropertyAccessExpression
import org.jetbrains.kotlin.fir.expressions.builder.buildVariableAssignment
import org.jetbrains.kotlin.fir.expressions.toResolvedCallableReference
import org.jetbrains.kotlin.fir.expressions.unwrapArgument
import org.jetbrains.kotlin.fir.extensions.FirAssignExpressionAltererExtension
import org.jetbrains.kotlin.fir.extensions.FirExtensionApiInternals
import org.jetbrains.kotlin.fir.extensions.FirFunctionCallRefinementExtension
import org.jetbrains.kotlin.fir.references.builder.buildSimpleNamedReference
import org.jetbrains.kotlin.fir.references.symbol
import org.jetbrains.kotlin.fir.resolve.calls.candidate.CallInfo
import org.jetbrains.kotlin.fir.resolve.providers.symbolProvider
import org.jetbrains.kotlin.fir.symbols.impl.FirFunctionSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirNamedFunctionSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirRegularClassSymbol
import org.jetbrains.kotlin.fir.types.builder.buildResolvedTypeRef
import org.jetbrains.kotlin.fir.types.builder.buildTypeProjectionWithVariance
import org.jetbrains.kotlin.fir.types.resolvedType
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.types.ConstantValueKind
import org.jetbrains.kotlin.types.Variance
import org.jetbrains.kotlin.utils.addToStdlib.firstIsInstance

val CONTEXTS_CALLABLE_ID = CallableId(PACKAGE_FQNAME, Name.identifier("contexts"))
val GET_CONTEXT_HERE_CALLABLE_ID = CallableId(PACKAGE_FQNAME, Name.identifier("getContextHere"))
val INSERT_HERE_CALLABLE_ID = CallableId(PACKAGE_FQNAME, Name.identifier("insertContextCallHere"))

private fun FqName.pathAsReceiver(src: KtSourceElement?): FirExpression? =
  PACKAGE_FQNAME.pathSegments().fold(null) { acc, name ->
    buildPropertyAccessExpression {
      source = src
      calleeReference = buildSimpleNamedReference {
        source = src
        this.name = name
      }
      explicitReceiver = acc
    }
  }

class WitherAssignmentAlterer(session: FirSession) : FirAssignExpressionAltererExtension(session) {
  override fun transformVariableAssignment(
    variableAssignment: FirVariableAssignment
  ): FirStatement? {
    if (variableAssignment.lValue.toResolvedCallableReference(session)?.symbol != insertHereSymbol)
      return null
    val vararg =
      (variableAssignment.rValue as? FirFunctionCall)?.argumentList?.arguments ?: return null
    return buildFunctionCall {
      source = variableAssignment.source
      coneTypeOrNull = session.builtinTypes.unitType.coneType
      // io.github.kyay10.wither.context(...)
      calleeReference = buildSimpleNamedReference {
        source = variableAssignment.source
        name = CONTEXT_CALLABLE_ID.callableName
      }
      explicitReceiver = PACKAGE_FQNAME.pathAsReceiver(variableAssignment.source)

      // (getContextHere<A>(0), getContextHere<B>(1), ...)
      argumentList = buildArgumentList {
        for ((index, arg) in vararg.withIndex()) {
          this.arguments.add(
            buildFunctionCall {
              source = variableAssignment.source
              calleeReference = buildSimpleNamedReference {
                source = variableAssignment.source
                name = GET_CONTEXT_HERE_CALLABLE_ID.callableName
              }
              explicitReceiver = PACKAGE_FQNAME.pathAsReceiver(variableAssignment.source)
              typeArguments.add(
                buildTypeProjectionWithVariance {
                  typeRef = buildResolvedTypeRef {
                    source = variableAssignment.source
                    coneType = arg.resolvedType
                  }
                  variance = Variance.INVARIANT
                }
              )
              argumentList = buildArgumentList {
                source = variableAssignment.source
                this.arguments.add(
                  buildLiteralExpression(
                    source = variableAssignment.source,
                    ConstantValueKind.Int,
                    index,
                    setType = true,
                  )
                )
              }
            }
          )
        }
      }
    }
  }

  private val insertHereSymbol by lazy {
    session.symbolProvider
      .getTopLevelPropertySymbols(
        INSERT_HERE_CALLABLE_ID.packageName,
        INSERT_HERE_CALLABLE_ID.callableName,
      )
      .first()
  }
}

@OptIn(FirExtensionApiInternals::class)
class WitherCallInjector(session: FirSession) : FirFunctionCallRefinementExtension(session) {
  override fun intercept(
    callInfo: CallInfo,
    symbol: FirNamedFunctionSymbol,
  ): CallReturnType? {
    if (symbol != contextsSymbol) return null
    // ad-hoc resolving the arguments because the function shape is pretty simple
    val arguments = callInfo.argumentList.arguments
    // call of the form `contexts(values = arrayOf(...)) {}` or `contexts(block = {}, values =
    // arrayOf(...))`. Not
    // gonna bother dealing with those.
    if (arguments.firstOrNull() is FirNamedArgumentExpression?) return null
    val lambda = arguments.last().unwrapArgument()
    if (lambda !is FirAnonymousFunctionExpression) return null
    val vararg = arguments.dropLast(1)
    val body = lambda.anonymousFunction.body ?: return null

    lambda.anonymousFunction.replaceBody(
      buildBlock {
        source = body.source

        statements.add(
          buildVariableAssignment {
            source = callInfo.callSite.source
            lValue = buildPropertyAccessExpression {
              source = callInfo.callSite.source
              explicitReceiver = PACKAGE_FQNAME.pathAsReceiver(callInfo.callSite.source)
              calleeReference = buildSimpleNamedReference {
                source = callInfo.callSite.source
                name = INSERT_HERE_CALLABLE_ID.callableName
              }
            }
            // this rValue will never be realized! It's just there to communicate information to the
            // assign alterer
            rValue = buildFunctionCall {
              source = callInfo.callSite.source
              argumentList = buildArgumentList { this.arguments.addAll(vararg) }
              calleeReference = buildSimpleNamedReference {
                source = callInfo.callSite.source
                name = INSERT_HERE_CALLABLE_ID.callableName
              }
            }
          }
        )
        statements.addAll(body.statements)
      }
    )
    return null
  }

  override fun transform(
    call: FirFunctionCall,
    originalSymbol: FirNamedFunctionSymbol,
  ): FirFunctionCall {
    error("should never be called")
  }

  override fun ownsSymbol(symbol: FirRegularClassSymbol): Boolean = false

  override fun anchorElement(symbol: FirRegularClassSymbol): KtSourceElement = symbol.source!!

  override fun restoreSymbol(
    call: FirFunctionCall,
    name: Name,
  ) = null

  private val contextsSymbol by lazy {
    session.symbolProvider
      .getTopLevelCallableSymbols(
        CONTEXTS_CALLABLE_ID.packageName,
        CONTEXTS_CALLABLE_ID.callableName,
      )
      .firstIsInstance<FirFunctionSymbol<*>>()
  }
}
