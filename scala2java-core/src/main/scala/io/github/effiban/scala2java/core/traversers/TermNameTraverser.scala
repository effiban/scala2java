package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.InternalTermNameTransformationContext
import io.github.effiban.scala2java.core.transformers.InternalTermNameTransformer
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Term

trait TermNameTraverser {
  def traverse(termName: Term.Name, context: InternalTermNameTransformationContext = InternalTermNameTransformationContext()): Unit
}

private[traversers] class TermNameTraverserImpl(expressionTermTraverser: => ExpressionTermTraverser,
                                                termNameTransformer: InternalTermNameTransformer)
                                               (implicit javaWriter: JavaWriter) extends TermNameTraverser {

  import javaWriter._

  override def traverse(termName: Term.Name, context: InternalTermNameTransformationContext = InternalTermNameTransformationContext()): Unit = {
    termNameTransformer.transform(termName, context) match {
      case name: Term.Name => write(name.value)
      case term: Term => expressionTermTraverser.traverse(term)
    }
  }
}
