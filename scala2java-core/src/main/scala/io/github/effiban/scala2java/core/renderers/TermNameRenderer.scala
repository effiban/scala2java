package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Term

trait TermNameRenderer extends JavaTreeRenderer[Term.Name]

private[renderers] class TermNameRendererImpl(implicit javaWriter: JavaWriter) extends TermNameRenderer {

  import javaWriter._

  override def render(termName: Term.Name): Unit = write(termName.value)
}
