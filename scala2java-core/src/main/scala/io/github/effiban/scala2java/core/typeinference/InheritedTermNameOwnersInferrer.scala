package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.reflection.ScalaReflectionUtils.isTermMemberOf

import scala.collection.MapView
import scala.meta.{Template, Term, Type}

trait InheritedTermNameOwnersInferrer {
  def infer(termName: Term.Name): MapView[Template, List[Type.Ref]]
}

private[typeinference] class InheritedTermNameOwnersInferrerImpl(enclosingTemplateAncestorsInferrer: EnclosingTemplateAncestorsInferrer)
  extends InheritedTermNameOwnersInferrer {

  override def infer(termName: Term.Name): MapView[Template, List[Type.Ref]] = {
    enclosingTemplateAncestorsInferrer.infer(termName)
      .view
      .mapValues { types => types.filter(tpe => isTermMemberOf(tpe, termName)) }
      .filter { case (_, types) => types.nonEmpty }
  }
}

object InheritedTermNameOwnersInferrer extends InheritedTermNameOwnersInferrerImpl(EnclosingTemplateAncestorsInferrer)