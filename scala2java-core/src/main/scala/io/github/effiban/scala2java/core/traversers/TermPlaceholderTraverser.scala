package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.entities.TraversalConstants.JavaPlaceholder
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Term

trait TermPlaceholderTraverser extends ScalaTreeTraverser[Term.Placeholder]

private[traversers] class TermPlaceholderTraverserImpl(implicit javaWriter: JavaWriter) extends TermPlaceholderTraverser {

  import javaWriter._

  // Underscore as expression - will compile in java only if it is an anonymous function, but rendering always
  override def traverse(ignored: Term.Placeholder): Unit = {
    write(JavaPlaceholder)
  }
}
