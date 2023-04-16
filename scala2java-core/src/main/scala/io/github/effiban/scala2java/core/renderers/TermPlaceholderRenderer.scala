package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.entities.TraversalConstants.JavaPlaceholder
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Term

trait TermPlaceholderRenderer extends TreeRenderer[Term.Placeholder]

private[renderers] class TermPlaceholderRendererImpl(implicit javaWriter: JavaWriter) extends TermPlaceholderRenderer {

  import javaWriter._

  override def render(ignored: Term.Placeholder): Unit = {
    write(JavaPlaceholder)
  }
}
