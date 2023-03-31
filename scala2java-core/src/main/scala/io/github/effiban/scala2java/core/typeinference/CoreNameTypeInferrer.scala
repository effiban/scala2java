package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.entities.{TermNameValues, TypeNameValues}
import io.github.effiban.scala2java.spi.typeinferrers.NameTypeInferrer

import scala.meta.{Term, Type}

object CoreNameTypeInferrer extends NameTypeInferrer {

  override def infer(termName: Term.Name): Option[Type] = {
    termName match {
      case Term.Name(TermNameValues.ScalaNone) => Some(Type.Name(TypeNameValues.ScalaOption))
      case Term.Name(TermNameValues.ScalaNil) => Some(Type.Name(TypeNameValues.List))
      case _ => None
    }
  }
}
