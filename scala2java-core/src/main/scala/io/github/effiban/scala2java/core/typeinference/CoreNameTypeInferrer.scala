package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.entities.TermNameValues
import io.github.effiban.scala2java.core.entities.TypeSelects.{ScalaList, ScalaOption}
import io.github.effiban.scala2java.spi.typeinferrers.NameTypeInferrer

import scala.meta.{Term, Type}

object CoreNameTypeInferrer extends NameTypeInferrer {

  override def infer(termName: Term.Name): Option[Type] = {
    termName match {
      case Term.Name(TermNameValues.ScalaNone) => Some(ScalaOption)
      case Term.Name(TermNameValues.ScalaNil) => Some(ScalaList)
      case _ => None
    }
  }
}
