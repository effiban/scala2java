package io.github.effiban.scala2java.core.desugarers.syntactic

import io.github.effiban.scala2java.core.desugarers.DifferentTypeDesugarer

import scala.meta.Term.For
import scala.meta.{Term, XtensionQuasiquoteTerm}

trait ForDesugarer extends DifferentTypeDesugarer[For, Term.Apply] with ForVariantDesugarer

private[syntactic] class ForDesugarerImpl(override val patToTermParamDesugarer: PatToTermParamDesugarer) extends ForDesugarer {

  private final val ForEachFunctionName: Term.Name = q"foreach"

  override val intermediateFunctionName: Term.Name = ForEachFunctionName
  override val finalFunctionName: Term.Name = ForEachFunctionName

  override def desugar(`for`: For): Term.Apply = desugar(`for`.enums, `for`.body)
}

object ForDesugarer extends ForDesugarerImpl(PatToTermParamDesugarer)