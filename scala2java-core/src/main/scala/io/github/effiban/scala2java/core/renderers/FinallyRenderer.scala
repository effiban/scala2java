package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Term
import scala.meta.Term.Block

trait FinallyRenderer extends JavaTreeRenderer[Term]

private[renderers] class FinallyRendererImpl(blockRenderer: => BlockRenderer)
                                            (implicit javaWriter: JavaWriter) extends FinallyRenderer {

  import javaWriter._

  // TODO support return value flag
  override def render(finallyp: Term): Unit = {
    write("finally")
    renderBody(finallyp)
  }

  private def renderBody(finallyp: Term): Unit = {
    val finallyBlock = finallyp match {
      case aBlock: Block => aBlock
      case _ => throw new IllegalStateException("A finally clause must be converted to a Block before this point")
    }
    blockRenderer.render(finallyBlock)
  }
}
