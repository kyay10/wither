package io.github.kyay10.wither.ir

import io.github.kyay10.wither.fir.WITH_CALLABLE_ID
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

class ScopeWithWithVariables(scope: Scope, irElement: IrElement) : ScopeWithIr(scope, irElement) {
  val withVariables: ArrayDeque<IrVariable> = ArrayDeque()
}

class WitherReceiverTransformer : IrElementTransformerVoidWithContext() {
  override fun createScope(declaration: IrSymbolOwner): ScopeWithIr =
    ScopeWithWithVariables(Scope(declaration.symbol), declaration)

  override fun visitContainerExpression(expression: IrContainerExpression) = visitStatementContainer(expression)

  fun <T : IrStatementContainer> visitStatementContainer(container: T) = withinBlockScope(container) {
    val iterator = container.statements.listIterator()
    for (statement in iterator) {
      val newStatement = statement.transform(this, null)
      if (newStatement is IrCall && newStatement.symbol.owner.callableId == WITH_CALLABLE_ID) {
        iterator.remove()
        val vararg = newStatement.arguments.single() as IrVararg
        for (arg in vararg.elements) {
          if (arg !is IrExpression) continue
          val variable = currentScope!!.scope.createTemporaryVariable(arg)
          iterator.add(variable)
          (currentScope!! as ScopeWithWithVariables).withVariables.addFirst(variable)
        }
      }
    }
    container
  }

  override fun visitBlockBody(body: IrBlockBody) = visitStatementContainer(body)

  override fun visitErrorCallExpression(expression: IrErrorCallExpression): IrExpression {
    if (expression.description != "Unresolved reference: this@R|<local>/<with receiver holder>|")
      return super.visitErrorCallExpression(expression)

    val variable = allScopes.asReversed().asSequence().flatMap { (it as ScopeWithWithVariables).withVariables }.firstOrNull {
      it.type == expression.type
    } ?: return super.visitErrorCallExpression(expression)
    return IrGetValueImpl(expression.startOffset, expression.endOffset, variable.symbol)
  }

  private inline fun <T> withinBlockScope(expression: IrStatementContainer, fn: () -> T): T {
    val currentScope = currentScope!!
    allScopes.push(ScopeWithWithVariables(currentScope.scope, expression))
    val result = fn()
    unsafeLeaveScope()
    return result
  }
}
