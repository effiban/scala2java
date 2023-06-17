package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.entities.TermNameValues.ScalaClassOf
import io.github.effiban.scala2java.core.renderers.ClassOfRenderer

import scala.meta.Term
import scala.meta.Term.ApplyType

@deprecated
trait DeprecatedMainApplyTypeTraverser extends ScalaTreeTraverser[Term.ApplyType]

@deprecated
private[traversers] class DeprecatedMainApplyTypeTraverserImpl(classOfTraverser: => DeprecatedClassOfTraverser,
                                                               classOfRenderer: => ClassOfRenderer,
                                                               standardApplyTypeTraverser: => DeprecatedStandardApplyTypeTraverser)
  extends DeprecatedMainApplyTypeTraverser {

  // parametrized type application, e.g.: classOf[X], identity[X]
  override def traverse(termApplyType: ApplyType): Unit = termApplyType.fun match {
    case Term.Name(ScalaClassOf) =>
      val traversedClassOf = classOfTraverser.traverse(termApplyType)
      classOfRenderer.render(traversedClassOf)
    case _ => standardApplyTypeTraverser.traverse(termApplyType)
  }
}
