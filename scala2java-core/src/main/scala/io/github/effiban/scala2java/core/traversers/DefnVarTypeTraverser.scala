package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.typeinference.TermTypeInferrer

import scala.meta.{Term, Type}

trait DefnVarTypeTraverser {
  def traverse(maybeDeclType: Option[Type], rhs: Option[Term]): Option[Type]
}

private[traversers] class DefnVarTypeTraverserImpl(typeTraverser: => TypeTraverser,
                                                   termTypeInferrer: => TermTypeInferrer) extends DefnVarTypeTraverser {

  override def traverse(maybeDeclType: Option[Type] = None, maybeRhs: Option[Term] = None): Option[Type] = {
    (maybeDeclType, maybeRhs) match {
      case (Some(declType), _) => Some(typeTraverser.traverse(declType))
      case (None, Some(rhs)) => inferTypeIfPossible(rhs)
      case _ => None
    }
  }

  private def inferTypeIfPossible(rhs: Term) = {
    termTypeInferrer.infer(rhs).map(typeTraverser.traverse)
  }
}
