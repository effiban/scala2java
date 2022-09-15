package effiban.scala2java.transformers

import scala.meta.Term.{Apply, Select}
import scala.meta.{Lit, Term}

trait TermInterpolateTransformer {
  def transform(termInterpolate: Term.Interpolate): Option[Term.Apply]
}

object TermInterpolateTransformer extends TermInterpolateTransformer {

  override def transform(termInterpolate: Term.Interpolate): Option[Term.Apply] = {
    termInterpolate.prefix match {
      case Term.Name("s") => Some(toJavaStringFormat(termInterpolate, implicitFormatSpecifier = "%s"))
      case Term.Name("f") | Term.Name("raw") => Some(toJavaStringFormat(termInterpolate))
      case _ => None
    }
  }

  private def toJavaStringFormat(termInterpolate: Term.Interpolate, implicitFormatSpecifier: String = "") = {
    val concatenatedParts = termInterpolate.parts.map(_.value).mkString(implicitFormatSpecifier)
    Apply(
      fun = Select(Term.Name("String"), Term.Name("format")),
      args = List(Lit.String(concatenatedParts)) ++ termInterpolate.args
    )
  }
}
