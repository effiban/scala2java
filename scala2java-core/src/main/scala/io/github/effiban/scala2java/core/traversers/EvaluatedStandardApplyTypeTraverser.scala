package io.github.effiban.scala2java.core.traversers

import scala.meta.Term
import scala.meta.Term.ApplyType

private[traversers] class EvaluatedStandardApplyTypeTraverser(termApplyTraverser: => TermApplyTraverser,
                                                              defaultStandardApplyTypeTraverser: => StandardApplyTypeTraverser)
  extends StandardApplyTypeTraverser {

  // parametrized type application which might need to be 'desugared' into a method invocation
  override def traverse(termApplyType: ApplyType): Unit = termApplyType.fun match {
    case fun@(_: Term.Name | _: Term.Select) => termApplyTraverser.traverse(Term.Apply(fun, Nil))
    case _ => defaultStandardApplyTypeTraverser.traverse(termApplyType)
  }
}
