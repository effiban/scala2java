package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Name

trait NameIndeterminateRenderer extends TreeRenderer[Name.Indeterminate]

class NameIndeterminateRendererImpl(implicit javaWriter: JavaWriter) extends NameIndeterminateRenderer {

  import javaWriter._

  override def render(indeterminateName: Name.Indeterminate): Unit = {
    write(indeterminateName.value)
  }
}
