package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Self

trait SelfRenderer extends JavaTreeRenderer[Self]

private[renderers] class SelfRendererImpl(implicit javaWriter: JavaWriter) extends SelfRenderer {

  import javaWriter._

  override def render(`self`: Self): Unit = {
    self.decltpe.foreach(_ => {
      writeComment(s"extends ${self.toString()}")
    })
  }
}
