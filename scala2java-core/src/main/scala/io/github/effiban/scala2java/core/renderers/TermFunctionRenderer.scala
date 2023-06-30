package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.contexts.{BlockRenderContext, SimpleBlockStatRenderContext, TermFunctionRenderContext, TermParamListRenderContext}
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Term
import scala.meta.Term.Block

trait TermFunctionRenderer {
  def render(function: Term.Function, context: TermFunctionRenderContext = TermFunctionRenderContext()): Unit
}

private[renderers] class TermFunctionRendererImpl(termParamRenderer: => TermParamRenderer,
                                                  termParamListRenderer: => TermParamListRenderer,
                                                  blockRenderer: => BlockRenderer,
                                                  defaultTermRenderer: => DefaultTermRenderer)
                                                 (implicit javaWriter: JavaWriter) extends TermFunctionRenderer {

  import javaWriter._

  // lambda definition
  override def render(function: Term.Function, context: TermFunctionRenderContext = TermFunctionRenderContext()): Unit = {
    function.params match {
      case param :: Nil if param.decltpe.isEmpty => termParamRenderer.render(param)
      case _ =>
        termParamListRenderer.render(
          termParams = function.params,
          context = TermParamListRenderContext(onSameLine = true)
        )
    }
    writeArrow()
    function.body match {
      case block: Block => blockRenderer.render(
        block = block,
        context = BlockRenderContext(lastStatContext = SimpleBlockStatRenderContext(context.uncertainReturn))
      )
      case term => defaultTermRenderer.render(term)
    }
  }
}
