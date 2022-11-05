package io.github.effiban.scala2java.core.typeinference

import scala.meta.Term.If
import scala.meta.{Lit, Type}

trait IfTypeInferrer extends TypeInferrer[If]

private[typeinference] class IfTypeInferrerImpl(termTypeInferrer: => TermTypeInferrer) extends IfTypeInferrer {

  override def infer(`if`: If): Option[Type] = {
    `if`.elsep match {
      // This is the case where there is no 'else' clause at all - so there cannot be a defined type
      case Lit.Unit() => Some(Type.AnonymousName())
      case _ => inferByComparison(`if`)
    }
  }

  private def inferByComparison(`if`: If) = {
    val maybeThenType = termTypeInferrer.infer(`if`.thenp)
    val maybeElseType = termTypeInferrer.infer(`if`.elsep)

    (maybeThenType, maybeElseType) match {
      case (Some(thenType), Some(Type.AnonymousName())) => Some(thenType)
      case (Some(Type.AnonymousName()), Some(elseType)) => Some(elseType)
      case (Some(thenType), Some(elseType)) if thenType.structure == elseType.structure => Some(thenType)
      // TODO - can improve by finding a common type
      case _ => None
    }
  }
}

