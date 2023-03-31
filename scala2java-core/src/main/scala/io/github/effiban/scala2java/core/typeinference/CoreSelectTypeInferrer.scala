package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.spi.contexts.TermSelectInferenceContext
import io.github.effiban.scala2java.spi.typeinferrers.SelectTypeInferrer

import scala.meta.{Term, Type}

object CoreSelectTypeInferrer extends SelectTypeInferrer {

  override def infer(termSelect: Term.Select, context: TermSelectInferenceContext): Option[Type] = {
    // TODO - add inferrable qualified names that are not method invocations
    None
  }
}