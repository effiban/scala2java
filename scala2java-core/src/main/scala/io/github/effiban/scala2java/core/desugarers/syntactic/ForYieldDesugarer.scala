package io.github.effiban.scala2java.core.desugarers.syntactic

import io.github.effiban.scala2java.core.desugarers.DifferentTypeDesugarer

import scala.meta.Term
import scala.meta.Term.ForYield

trait ForYieldDesugarer extends DifferentTypeDesugarer[ForYield, Term.Apply] with ForVariantDesugarer

private[syntactic] class ForYieldDesugarerImpl(override val patToTermParamDesugarer: PatToTermParamDesugarer) extends ForYieldDesugarer {

  override val intermediateFunctionName: Term.Name = Term.Name("flatMap")
  override val finalFunctionName: Term.Name = Term.Name("map")

  override def desugar(forYield: ForYield): Term.Apply = desugar(forYield.enums, forYield.body)
}

object ForYieldDesugarer extends ForYieldDesugarerImpl(PatToTermParamDesugarer)