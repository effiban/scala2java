package effiban.scala2java.traversers

import effiban.scala2java.entities.TraversalConstants.JavaPlaceholder
import effiban.scala2java.writers.JavaWriter

import scala.meta.Term

trait TermPlaceholderTraverser extends ScalaTreeTraverser[Term.Placeholder]

private[traversers] class TermPlaceholderTraverserImpl(implicit javaWriter: JavaWriter) extends TermPlaceholderTraverser {

  import javaWriter._

  // Underscore as expression - will compile in java only if it is an anonymous function, but rendering always
  override def traverse(ignored: Term.Placeholder): Unit = {
    write(JavaPlaceholder)
  }
}

object TermPlaceholderTraverser extends TermPlaceholderTraverserImpl
