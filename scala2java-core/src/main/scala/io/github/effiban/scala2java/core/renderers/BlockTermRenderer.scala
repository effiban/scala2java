package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.classifiers.JavaStatClassifier
import io.github.effiban.scala2java.core.entities.TraversalConstants.UncertainReturn
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Term

// TODO merge with BlockStatRenderer when ready
trait BlockTermRenderer {

  def render(term: Term): Unit

  def renderLast(term: Term, uncertainReturn: Boolean = false): Unit
}

private[renderers] class BlockTermRendererImpl(expressionTermRefRenderer: => ExpressionTermRefRenderer,
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
    if (uncertainReturn) {
      writeComment(UncertainReturn)
    }
    render(term)
  }

  private def writeStatEnd(term: Term): Unit = {
    if (javaStatClassifier.requiresEndDelimiter(term)) {
      writeStatementEnd()
    }
  }
}
