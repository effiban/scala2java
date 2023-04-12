package io.github.effiban.scala2java.core.traversers

import scala.meta.Term
import scala.meta.Term.ApplyType

private[traversers] class EvaluatedStandardApplyTypeTraverser(termApplyTraverser: => TermApplyTraverser)
  extends StandardApplyTypeTraverser {

  // parametrized type application which is called in a context where it should be 'desugared' into a method invocation
  override def traverse(termApplyType: ApplyType): Unit = termApplyTraverser.traverse(Term.Apply(termApplyType, Nil))
}
