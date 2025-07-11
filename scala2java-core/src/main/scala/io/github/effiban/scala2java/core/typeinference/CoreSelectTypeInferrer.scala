package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.entities.TermSelects.{ScalaNil, ScalaNone}
import io.github.effiban.scala2java.core.entities.TreeKeyedMap
import io.github.effiban.scala2java.core.entities.TypeSelects.{ScalaList, ScalaOption}
import io.github.effiban.scala2java.spi.contexts.TermSelectInferenceContext
import io.github.effiban.scala2java.spi.typeinferrers.SelectTypeInferrer

import scala.meta.{Term, Type}


object CoreSelectTypeInferrer extends SelectTypeInferrer {

  private final val TermSelectToType = Map[Term.Select, Type](
    ScalaNil -> ScalaList,
    ScalaNone -> ScalaOption
  )

  override def infer(termSelect: Term.Select, context: TermSelectInferenceContext): Option[Type] = {
    TreeKeyedMap.get(TermSelectToType, termSelect)
  }
}