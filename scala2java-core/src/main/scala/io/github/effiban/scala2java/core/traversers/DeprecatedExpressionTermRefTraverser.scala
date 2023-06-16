package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.DefaultTermRefRenderer

import scala.meta.Term
import scala.meta.Term.ApplyUnary

@deprecated
trait DeprecatedExpressionTermRefTraverser extends DeprecatedTermRefTraverser

@deprecated
private[traversers] class DeprecatedExpressionTermRefTraverserImpl(termNameTraverser: => DeprecatedTermNameTraverser,
                                                                   expressionTermSelectTraverser: => DeprecatedExpressionTermSelectTraverser,
                                                                   applyUnaryTraverser: => DeprecatedApplyUnaryTraverser,
                                                                   defaultTermRefTraverser: => DefaultTermRefTraverser,
                                                                   defaultTermRefRenderer: => DefaultTermRefRenderer)
  extends DeprecatedExpressionTermRefTraverser {

  override def traverse(termRef: Term.Ref): Unit = termRef match {
    case termName: Term.Name => termNameTraverser.traverse(termName)
    case termSelect: Term.Select => expressionTermSelectTraverser.traverse(termSelect)
    case applyUnary: ApplyUnary => applyUnaryTraverser.traverse(applyUnary)
    case aTermRef =>
      val traversedTermRef = defaultTermRefTraverser.traverse(aTermRef)
      defaultTermRefRenderer.render(traversedTermRef)
  }
}
