package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.qualifiers.QualificationContext
import io.github.effiban.scala2java.core.reflection.ScalaReflectionLookup

import scala.collection.immutable.ListMap
import scala.meta.{Template, Term, Type}

trait InheritedTermNameOwnersInferrer {
  def infer(termName: Term.Name, template: Template, context: QualificationContext = QualificationContext()): List[Type.Ref]

  def inferAll(termName: Term.Name, context: QualificationContext = QualificationContext()): ListMap[Template, List[Type.Ref]]
}

private[typeinference] class InheritedTermNameOwnersInferrerImpl(enclosingTemplateAncestorsInferrer: EnclosingTemplateAncestorsInferrer,
                                                                 templateAncestorsInferrer: TemplateAncestorsInferrer,
                                                                 scalaReflectionLookup: ScalaReflectionLookup)
  extends InheritedTermNameOwnersInferrer {

  override def infer(termName: Term.Name, template: Template, context: QualificationContext): List[Type.Ref] = {
    templateAncestorsInferrer.infer(template, context)
      .filter(tpe => scalaReflectionLookup.isTermMemberOf(tpe, termName))
  }

  override def inferAll(termName: Term.Name, context: QualificationContext = QualificationContext()): ListMap[Template, List[Type.Ref]] = {
    ListMap.from(enclosingTemplateAncestorsInferrer.infer(termName, context)
      .view
      .mapValues { types => types.filter(tpe => scalaReflectionLookup.isTermMemberOf(tpe, termName)) }
      .filter { case (_, types) => types.nonEmpty })
  }

}

object InheritedTermNameOwnersInferrer extends InheritedTermNameOwnersInferrerImpl(
  EnclosingTemplateAncestorsInferrer,
  TemplateAncestorsInferrer,
  ScalaReflectionLookup
)