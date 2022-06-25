package com.effiban.scala2java.transformers

import scala.meta.Term.{Apply, Select}
import scala.meta.{Lit, Term}

trait TermInterpolateTransformer {
  def transform(termInterpolate: Term.Interpolate): Option[Term.Apply]
}

object TermInterpolateTransformer extends TermInterpolateTransformer {

  override def transform(termInterpolate: Term.Interpolate): Option[Term.Apply] = {
    termInterpolate.prefix match {
      // Transform Scala "s" interpolation into Java String.format(...)
      case Term.Name("s") => Some(toJavaStringFormat(termInterpolate))
      // TODO handle other interpolations
      case _ => None
    }

  }

  private def toJavaStringFormat(termInterpolate: Term.Interpolate) = {
    Apply(
      fun = Select(Term.Name("String"), Term.Name("format")),
      args = List(Lit.String(termInterpolate.parts.mkString("%s"))) ++ termInterpolate.args
    )
  }
}
