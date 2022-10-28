package io.github.effiban.scala2java.traversers

import io.github.effiban.scala2java.transformers.TermNameTransformer
import io.github.effiban.scala2java.writers.JavaWriter

import scala.meta.Term

trait TermNameTraverser extends ScalaTreeTraverser[Term.Name]

private[traversers] class TermNameTraverserImpl(termTraverser: => TermTraverser,
                                                termNameTransformer: TermNameTransformer)
                                               (implicit javaWriter: JavaWriter) extends TermNameTraverser {

  import javaWriter._

  override def traverse(termName: Term.Name): Unit = {
    termNameTransformer.transform(termName) match {
      case name: Term.Name => write(name.value)
      case term: Term => termTraverser.traverse(term)
    }
  }
}
