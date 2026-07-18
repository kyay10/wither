package io.github.kyay10.wither.fir

import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.fir.FirElement
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.expression.ExpressionCheckers
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirBasicExpressionChecker
import org.jetbrains.kotlin.fir.analysis.extensions.FirAdditionalCheckersExtension
import org.jetbrains.kotlin.fir.declarations.FirDeclarationOrigin
import org.jetbrains.kotlin.fir.expressions.FirContextArgumentListOwner
import org.jetbrains.kotlin.fir.expressions.FirPropertyAccessExpression
import org.jetbrains.kotlin.fir.expressions.FirStatement
import org.jetbrains.kotlin.fir.expressions.builder.buildThisReceiverExpression
import org.jetbrains.kotlin.fir.expressions.toResolvedCallableReference
import org.jetbrains.kotlin.fir.references.builder.buildImplicitThisReference
import org.jetbrains.kotlin.fir.symbols.impl.FirValueParameterSymbol
import org.jetbrains.kotlin.fir.types.coneTypeOrNull
import org.jetbrains.kotlin.fir.visitors.FirDefaultTransformer

class WitherContextArgumentCleaner(session: FirSession) : FirAdditionalCheckersExtension(session) {
  override val expressionCheckers: ExpressionCheckers
    get() =
      object : ExpressionCheckers() {
        override val basicExpressionCheckers: Set<FirBasicExpressionChecker>
          get() =
            setOf(
              object : FirBasicExpressionChecker(MppCheckerKind.Common) {
                context(context: CheckerContext, reporter: DiagnosticReporter)
                override fun check(expression: FirStatement) {
                  if (expression !is FirContextArgumentListOwner) return
                  expression.transformContextArguments(
                    WitherContextArgumentCleanerTransformer,
                    Unit,
                  )
                }
              }
            )
      }
}

object WitherContextArgumentCleanerTransformer : FirDefaultTransformer<Unit>() {
  override fun <E : FirElement> transformElement(
    element: E,
    data: Unit,
  ): E {
    element.transformChildren(this, data)
    return element
  }

  override fun transformPropertyAccessExpression(
    propertyAccessExpression: FirPropertyAccessExpression,
    data: Unit,
  ): FirStatement {
    run default@{
      val reference = propertyAccessExpression.toResolvedCallableReference() ?: return@default
      val symbol = reference.resolvedSymbol as? FirValueParameterSymbol ?: return@default
      if (
        (symbol.origin as? FirDeclarationOrigin.Plugin)?.key !=
          WitherImplicitValueGenerator.GeneratedReceiverFromWithKey
      )
        return@default
      val fakeReceiver = symbol.fakeLegacyReceiver ?: return@default
      return buildThisReceiverExpression {
        coneTypeOrNull = fakeReceiver.typeRef.coneTypeOrNull
        isImplicit = true
        source = propertyAccessExpression.source
        nonFatalDiagnostics.addAll(propertyAccessExpression.nonFatalDiagnostics)
        calleeReference = buildImplicitThisReference {
          boundSymbol = fakeReceiver.symbol
        }
      }
    }
    return super.transformPropertyAccessExpression(
      propertyAccessExpression,
      data,
    )
  }
}
