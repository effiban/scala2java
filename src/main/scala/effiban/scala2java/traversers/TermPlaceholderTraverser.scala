package effiban.scala2java.traversers

import effiban.scala2java.JavaEmitter
import effiban.scala2java.entities.TraversalConstants.JavaPlaceholder

import scala.meta.Term

trait TermPlaceholderTraverser extends ScalaTreeTraverser[Term.Placeholder]

private[scala2java] class TermPlaceholderTraverserImpl(implicit javaEmitter: JavaEmitter) extends TermPlaceholderTraverser {

  import javaEmitter._

  // Underscore as expression - will compile in java only if it is an anonymous function, but rendering always
  override def traverse(ignored: Term.Placeholder): Unit = {
    emit(JavaPlaceholder)
  }
}

object TermPlaceholderTraverser extends TermPlaceholderTraverserImpl
