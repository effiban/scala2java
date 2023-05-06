package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.entities.TermNameValues.ScalaClassOf

import scala.meta.Term
import scala.meta.Term.ApplyType

trait MainApplyTypeTraverser extends ScalaTreeTraverser[Term.ApplyType]

private[traversers] class MainApplyTypeTraverserImpl(classOfTraverser: => ClassOfTraverser,
                                                     standardApplyTypeTraverser: => StandardApplyTypeTraverser)
  extends MainApplyTypeTraverser {

  // parametrized type application, e.g.: classOf[X], identity[X]
  override def traverse(termApplyType: ApplyType): Unit = termApplyType.fun match {
    case Term.Name(ScalaClassOf) => classOfTraverser.traverse(termApplyType)
    case _ => standardApplyTypeTraverser.traverse(termApplyType)
  }
}
