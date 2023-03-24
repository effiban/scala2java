package io.github.effiban.scala2java.spi.typeinferrers

import io.github.effiban.scala2java.spi.contexts.TermApplyInferenceContext
import io.github.effiban.scala2java.spi.entities.PartialDeclDef

import scala.meta.Term

trait ApplyDeclDefInferrer {

  def infer(termApply: Term.Apply, context: TermApplyInferenceContext): PartialDeclDef
}

object ApplyDeclDefInferrer {

  /** The empty inferrer which returns an empty object, meaning nothing could be inferred. */
  val Empty: ApplyDeclDefInferrer = (_, _) => PartialDeclDef()
}