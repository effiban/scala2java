package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.reflection.ScalaReflectionUtils.{asScalaMetaTypeRef, baseClassesOf, classSymbolOf, isTermMemberOf}

import scala.meta.{Member, Term, Type}

trait InheritedTermNameOwnersInferrer {
  def infer(termName: Term.Name): Map[Member, List[Type.Ref]]
}

private[typeinference] class InheritedTermNameOwnersInferrerImpl(enclosingMemberPathsInferrer: EnclosingMemberPathsInferrer)
  extends InheritedTermNameOwnersInferrer {

  override def infer(termName: Term.Name): Map[Member, List[Type.Ref]] = {
    enclosingMemberPathsInferrer.infer(termName)
      .map(memberPath => (memberPath.last, classSymbolOf(memberPath)))
      .collect { case (member, Some(cls)) => (member, cls) }
      .map { case (member, cls) => (member, baseClassesOf(cls)) }
      .map { case (member, classes) => (member, classes.filter(cls => isTermMemberOf(cls, termName))) }
      .map { case (member, classes) => (member, classes.map(asScalaMetaTypeRef)) }
      .map { case (member, classes) => (member, classes.collect {case Some(typeRef) => typeRef }) }
      .filter { case (_, classes) => classes.nonEmpty }
      .toMap
  }
}

object InheritedTermNameOwnersInferrer extends InheritedTermNameOwnersInferrerImpl(EnclosingMemberPathsInferrer)