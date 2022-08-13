package effiban.scala2java.typeinference

import scala.meta.Term.If
import scala.meta.{Lit, Type}

trait IfTypeInferrer extends TypeInferrer[If]

private[typeinference] class IfTypeInferrerImpl(termTypeInferrer: => TermTypeInferrer) extends IfTypeInferrer {

  override def infer(`if`: If): Option[Type] = {
    val maybeThenType = termTypeInferrer.infer(`if`.thenp)
    val maybeElseType = termTypeInferrer.infer(`if`.elsep)
    (`if`.thenp, `if`.elsep) match {
      case (_, Lit.Unit()) => None
      case _ => typeIfEqual(maybeThenType, maybeElseType)
    }
  }

  private def typeIfEqual(maybeThenType: Option[Type], maybeElseType: Option[Type]) =
    (maybeThenType, maybeElseType) match {
      case (Some(thenType), Some(elseType)) if thenType.structure == elseType.structure => Some(thenType)
      case _ => None
    }
}

