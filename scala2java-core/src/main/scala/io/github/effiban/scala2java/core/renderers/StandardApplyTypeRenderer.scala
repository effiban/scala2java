package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.contexts.TermSelectContext
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Term
import scala.meta.Term.ApplyType

trait StandardApplyTypeRenderer extends JavaTreeRenderer[Term.ApplyType]

private[renderers] class StandardApplyTypeRendererImpl(expressionTermSelectRenderer: => ExpressionTermSelectRenderer,
                                                       typeListRenderer: => TypeListRenderer,
                                                       expressionTermRenderer: => ExpressionTermRenderer)
                                                      (implicit javaWriter: JavaWriter) extends StandardApplyTypeRenderer {

  import javaWriter._

  override def render(applyType: Term.ApplyType): Unit = applyType.fun match {
    case termSelect: Term.Select => expressionTermSelectRenderer.render(termSelect, TermSelectContext(applyType.targs))
    case term => renderUnqualified(applyType, term)
  }

  private def renderUnqualified(applyType: ApplyType, term: Term): Unit = {
    // In Java a type can only be applied to a qualified name, so the best we can do is guess the qualifier in a comment
    writeComment("this?")
    writeQualifierSeparator()
    typeListRenderer.render(applyType.targs)
    expressionTermRenderer.render(term)
  }
}