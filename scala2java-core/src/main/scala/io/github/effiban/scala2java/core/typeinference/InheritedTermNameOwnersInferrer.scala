package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.qualifiers.QualificationContext
import io.github.effiban.scala2java.core.reflection.ScalaReflectionUtils.isTermMemberOf

import scala.collection.immutable.ListMap
import scala.meta.{Template, Term, Type}

trait InheritedTermNameOwnersInferrer {
  def infer(termName: Term.Name, context: QualificationContext = QualificationContext()): ListMap[Template, List[Type.Ref]]
}

private[typeinference] class InheritedTermNameOwnersInferrerImpl(enclosingTemplateAncestorsInferrer: EnclosingTemplateAncestorsInferrer)
  extends InheritedTermNameOwnersInferrer {

  override def infer(termName: Term.Name, context: QualificationContext = QualificationContext()): ListMap[Template, List[Type.Ref]] = {
    ListMap.from(enclosingTemplateAncestorsInferrer.infer(termName, context)
      .view
      .mapValues { types => types.filter(tpe => isTermMemberOf(tpe, termName)) }
      .filter { case (_, types) => types.nonEmpty })
  }
}

object InheritedTermNameOwnersInferrer extends InheritedTermNameOwnersInferrerImpl(EnclosingTemplateAncestorsInferrer)