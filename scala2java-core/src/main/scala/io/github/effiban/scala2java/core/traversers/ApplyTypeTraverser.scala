package io.github.effiban.scala2java.core.traversers

import scala.meta.Term
import scala.meta.Term.ApplyType

trait ApplyTypeTraverser extends ScalaTreeTraverser1[Term.ApplyType]

private[traversers] class ApplyTypeTraverserImpl(expressionTermTraverser: => ExpressionTermTraverser,
                                                 typeTraverser: => TypeTraverser) extends ApplyTypeTraverser {

  // parametrized type application which is the 'fun' of a method invocation
  override def traverse(termApplyType: ApplyType): Term.ApplyType = {
    val traversedFun = expressionTermTraverser.traverse(termApplyType.fun)
    val traversedTypes = termApplyType.targs.map(typeTraverser.traverse)
    Term.ApplyType(traversedFun, traversedTypes)
  }
}