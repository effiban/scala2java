package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.entities.TraversalConstants.JavaPlaceholder
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Term

trait TermPlaceholderRenderer extends JavaTreeRenderer[Term.Placeholder]

private[renderers] class TermPlaceholderRendererImpl(implicit javaWriter: JavaWriter) extends TermPlaceholderRenderer {

  import javaWriter._

  // Underscore as expression - will compile in java only if it is an anonymous function, but rendering always
  override def render(ignored: Term.Placeholder): Unit = {
    write(JavaPlaceholder)
  }
}
