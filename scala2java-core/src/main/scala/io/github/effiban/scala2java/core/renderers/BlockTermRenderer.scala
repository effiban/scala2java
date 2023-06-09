package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.classifiers.JavaStatClassifier
import io.github.effiban.scala2java.core.contexts.{IfRenderContext, TryRenderContext}
import io.github.effiban.scala2java.core.entities.TraversalConstants.UncertainReturn
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Term
import scala.meta.Term.{If, Try, TryWithHandler}

// TODO merge with BlockStatRenderer when ready
trait BlockTermRenderer {

  def render(term: Term): Unit

  def renderLast(term: Term, uncertainReturn: Boolean = false): Unit
}

private[renderers] class BlockTermRendererImpl(expressionTermRefRenderer: => ExpressionTermRefRenderer,
                                               ifRenderer: => IfRenderer,
                                               tryRenderer: => TryRenderer,
                                               tryWithHandlerRenderer: => TryWithHandlerRenderer,
                                               defaultTermRenderer: => DefaultTermRenderer,
                                               javaStatClassifier: JavaStatClassifier)
                                              (implicit javaWriter: JavaWriter) extends BlockTermRenderer {

  import javaWriter._

  override def render(term: Term): Unit = {
    term match {
      case termRef: Term.Ref => expressionTermRefRenderer.render(termRef)
      case aTerm => defaultTermRenderer.render(aTerm)
    }
    writeStatEnd(term)
  }

  override def renderLast(term: Term, uncertainReturn: Boolean = false): Unit = {
    term match {
      case `if`: If => ifRenderer.render(`if`, context = IfRenderContext(uncertainReturn = uncertainReturn))
      case `try`: Try => tryRenderer.render(`try`, context = TryRenderContext(uncertainReturn = uncertainReturn))
      case tryWithHandler: TryWithHandler =>
        tryWithHandlerRenderer.render(tryWithHandler, context = TryRenderContext(uncertainReturn = uncertainReturn))
      case _ if uncertainReturn =>
        writeComment(UncertainReturn)
        render(term)
      case _ => render(term)
    }
  }

  private def writeStatEnd(term: Term): Unit = {
    if (javaStatClassifier.requiresEndDelimiter(term)) {
      writeStatementEnd()
    }
  }
}
