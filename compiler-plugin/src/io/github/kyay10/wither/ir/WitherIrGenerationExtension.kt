package io.github.kyay10.wither.ir

import io.github.kyay10.wither.fir.CALLABLE_IDS
import io.github.kyay10.wither.fir.CALLABLE_NAMES
import io.github.kyay10.wither.fir.CONTEXT_CALLABLE_ID
import io.github.kyay10.wither.fir.HOLDER_NAMES
import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.ScopeWithIr
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.push
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.builders.Scope
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.IrSymbolOwner
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.expressions.IrBlockBody
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrContainerExpression
import org.jetbrains.kotlin.ir.expressions.IrErrorCallExpression
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrStatementContainer
import org.jetbrains.kotlin.ir.expressions.IrVararg
import org.jetbrains.kotlin.ir.expressions.impl.IrGetValueImpl
import org.jetbrains.kotlin.ir.util.callableId
import org.jetbrains.kotlin.ir.visitors.transformChildrenVoid

class WitherIrGenerationExtension : IrGenerationExtension {
  override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
    moduleFragment.transformChildrenVoid(WitherReceiverTransformer())
  }
}

class ScopeWithWitherVariables(scope: Scope, irElement: IrElement) : ScopeWithIr(scope, irElement) {
  val withVariables: ArrayDeque<IrVariable> = ArrayDeque()
  val contextVariables: ArrayDeque<IrVariable> = ArrayDeque()
}

val ERROR_DESCRIPTIONS = CALLABLE_NAMES.associateBy {
  "Unresolved reference: this@R|<local>/${HOLDER_NAMES.getValue(it)}|"
}

class WitherReceiverTransformer : IrElementTransformerVoidWithContext() {
  override fun createScope(declaration: IrSymbolOwner): ScopeWithIr =
    ScopeWithWitherVariables(Scope(declaration.symbol), declaration)

  override fun visitContainerExpression(expression: IrContainerExpression) =
    visitStatementContainer(expression)

  fun <T : IrStatementContainer> visitStatementContainer(container: T) =
    withinBlockScope(container) {
      val iterator = container.statements.listIterator()
      for (statement in iterator) {
        val newStatement = statement.transform(this, null)
        if (newStatement is IrCall && newStatement.symbol.owner.callableId in CALLABLE_IDS) {
          iterator.remove()
          val vararg = newStatement.arguments.single() as IrVararg
          for (arg in vararg.elements) {
            if (arg !is IrExpression) continue
            val variable = currentScope!!.scope.createTemporaryVariable(arg)
            iterator.add(variable)
            if (newStatement.symbol.owner.callableId == CONTEXT_CALLABLE_ID)
              (currentScope!! as ScopeWithWitherVariables).contextVariables.addFirst(variable)
            else (currentScope!! as ScopeWithWitherVariables).withVariables.addFirst(variable)
          }
        }
      }
      container
    }

  override fun visitBlockBody(body: IrBlockBody) = visitStatementContainer(body)

  override fun visitErrorCallExpression(expression: IrErrorCallExpression): IrExpression {
    val witherName =
      ERROR_DESCRIPTIONS[expression.description]
        ?: return super.visitErrorCallExpression(expression)

    val variable =
      allScopes
        .asReversed()
        .asSequence()
        .flatMap {
          if (witherName == CONTEXT_CALLABLE_ID.callableName)
            (it as ScopeWithWitherVariables).contextVariables
          else (it as ScopeWithWitherVariables).withVariables
        }
        .firstOrNull {
          it.type == expression.type
        } ?: return super.visitErrorCallExpression(expression)
    return IrGetValueImpl(expression.startOffset, expression.endOffset, variable.symbol)
  }

  private inline fun <T> withinBlockScope(expression: IrStatementContainer, fn: () -> T): T {
    val currentScope = currentScope!!
    allScopes.push(ScopeWithWitherVariables(currentScope.scope, expression))
    val result = fn()
    unsafeLeaveScope()
    return result
  }
}
