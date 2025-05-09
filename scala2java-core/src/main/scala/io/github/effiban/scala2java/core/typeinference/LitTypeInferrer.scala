package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.entities.TypeSelects._
import io.github.effiban.scala2java.spi.typeinferrers.TypeInferrer0

import scala.meta.{Lit, Type}

trait LitTypeInferrer extends TypeInferrer0[Lit]

object LitTypeInferrer extends LitTypeInferrer {

  override def infer(lit: Lit): Option[Type] = {
    lit match {
      case _: Lit.Boolean => Some(ScalaBoolean)
      case _: Lit.Byte => Some(ScalaByte)
      case _: Lit.Short => Some(ScalaShort)
      case _: Lit.Int => Some(ScalaInt)
      case _: Lit.Long => Some(ScalaLong)
      case _: Lit.Float => Some(ScalaFloat)
      case _: Lit.Double => Some(ScalaDouble)
      case _: Lit.Char => Some(ScalaChar)
      case _: Lit.String => Some(JavaString)
      case _: Lit.Symbol => Some(JavaString)
      case _: Lit.Unit => Some(ScalaUnit)
      case _: Lit.Null => Some(Type.AnonymousName())
      case _ => None
    }
  }
}
