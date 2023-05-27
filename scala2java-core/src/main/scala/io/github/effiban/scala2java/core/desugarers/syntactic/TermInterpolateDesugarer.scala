package io.github.effiban.scala2java.core.desugarers.syntactic

import io.github.effiban.scala2java.core.desugarers.DifferentTypeDesugarer

import scala.meta.{Lit, Term}

trait TermInterpolateDesugarer extends DifferentTypeDesugarer[Term.Interpolate, Term]

object TermInterpolateDesugarer extends TermInterpolateDesugarer {

  override def desugar(termInterpolate: Term.Interpolate): Term = {
    termInterpolate.prefix match {
      case Term.Name("s") => toJavaStringFormat(termInterpolate, implicitFormatSpecifier = "%s")
      case Term.Name("f") | Term.Name("raw") => toJavaStringFormat(termInterpolate)
      // TODO support custom interpolations
      case _ => termInterpolate
    }
  }

  private def toJavaStringFormat(termInterpolate: Term.Interpolate, implicitFormatSpecifier: String = "") = {
    val concatenatedParts = termInterpolate.parts.map(_.value).mkString(implicitFormatSpecifier)
    Term.Apply(
      fun = Term.Select(Term.Name("String"), Term.Name("format")),
      args = List(Lit.String(concatenatedParts)) ++ termInterpolate.args
    )
  }
}
