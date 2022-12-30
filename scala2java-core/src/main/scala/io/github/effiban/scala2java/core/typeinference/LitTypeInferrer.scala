package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.spi.typeinferrers.TypeInferrer

import scala.meta.{Lit, Type}

trait LitTypeInferrer extends TypeInferrer[Lit]

object LitTypeInferrer extends LitTypeInferrer {

  override def infer(lit: Lit): Option[Type] = {
    lit match {
      case _: Lit.Boolean => Some(Type.Name("Boolean"))
      case _: Lit.Byte => Some(Type.Name("Byte"))
      case _: Lit.Short => Some(Type.Name("Short"))
      case _: Lit.Int => Some(Type.Name("Int"))
      case _: Lit.Long => Some(Type.Name("Long"))
      case _: Lit.Float => Some(Type.Name("Float"))
      case _: Lit.Double => Some(Type.Name("Double"))
      case _: Lit.Char => Some(Type.Name("Char"))
      case _: Lit.String => Some(Type.Name("String"))
      case _: Lit.Symbol => Some(Type.Name("String"))
      case _: Lit.Unit => Some(Type.Name("Unit"))
      case _: Lit.Null => Some(Type.AnonymousName())
      case _ => None
    }
  }
}
