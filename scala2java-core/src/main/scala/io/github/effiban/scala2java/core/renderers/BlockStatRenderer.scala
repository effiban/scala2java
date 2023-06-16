package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.classifiers.JavaStatClassifier
import io.github.effiban.scala2java.core.contexts.{IfRenderContext, TryRenderContext}
import io.github.effiban.scala2java.core.entities.TraversalConstants.UncertainReturn
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Term.{If, Try, TryWithHandler}
import scala.meta.{Stat, Term}

trait BlockStatRenderer {

  def render(stat: Stat): Unit

  def renderLast(stat: Stat, uncertainReturn: Boolean = false): Unit
}

private[renderers] class BlockStatRendererImpl(expressionTermRefRenderer: => ExpressionTermRefRenderer,
                                               ifRenderer: => IfRenderer,
                                               tryRenderer: => TryRenderer,
                                               tryWithHandlerRenderer: => TryWithHandlerRenderer,
                                               defaultTermRenderer: => DefaultTermRenderer,
                                               javaStatClassifier: JavaStatClassifier)
                                              (implicit javaWriter: JavaWriter) extends BlockStatRenderer {

  import javaWriter._

  override def render(stat: Stat): Unit = {
    stat match {
      case termRef: Term.Ref => expressionTermRefRenderer.render(termRef)
      case aTerm: Term => defaultTermRenderer.render(aTerm)
      // TODO support stats
      case aStat: Stat => throw new UnsupportedOperationException("Rendering of a non-term stat in a block is not supported yet")
    }
    writeStatEnd(stat)
  }

  override def renderLast(stat: Stat, uncertainReturn: Boolean = false): Unit = {
    stat match {
      case `if`: If => ifRenderer.render(`if`, context = IfRenderContext(uncertainReturn = uncertainReturn))
      case `try`: Try => tryRenderer.render(`try`, context = TryRenderContext(uncertainReturn = uncertainReturn))
      case tryWithHandler: TryWithHandler =>
        tryWithHandlerRenderer.render(tryWithHandler, context = TryRenderContext(uncertainReturn = uncertainReturn))
      case _ if uncertainReturn =>
        writeComment(UncertainReturn)
        render(stat)
      case _ => render(stat)
    }
  }

  private def writeStatEnd(stat: Stat): Unit = {
    if (javaStatClassifier.requiresEndDelimiter(stat)) {
      writeStatementEnd()
    }
  }
}
