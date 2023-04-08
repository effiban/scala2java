package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.transformers.InternalTermNameTransformer
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Term

trait TermNameTraverser {
  def traverse(termName: Term.Name): Unit
}

private[traversers] class TermNameTraverserImpl(termTraverser: => TermTraverser,
                                                termNameTransformer: => InternalTermNameTransformer)
                                               (implicit javaWriter: JavaWriter) extends TermNameTraverser {

  import javaWriter._

  override def traverse(termName: Term.Name): Unit = {
    termNameTransformer.transform(termName) match {
      case name: Term.Name => write(name.value)
      case term: Term => termTraverser.traverse(term)
    }
  }
}
