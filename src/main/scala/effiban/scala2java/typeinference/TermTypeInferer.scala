package effiban.scala2java.typeinference

import scala.meta.Term.{Ascribe, New, Return}
import scala.meta.{Lit, Term, Type}

trait TermTypeInferer extends TypeInferer[Term]

private[typeinference] class TermTypeInfererImpl(litTypeInferer: LitTypeInferer) extends TermTypeInferer {

  override def infer(term: Term): Option[Type] = {
    term match {
      case lit: Lit => litTypeInferer.infer(lit)
      case `return`: Return => infer(`return`.expr)
      case ascribe: Ascribe => Some(ascribe.tpe)
      case `new`: New => Some(`new`.init.tpe)
      case repeated: Term.Repeated => inferRepeated(repeated)
      case _: Term.Interpolate => Some(Type.Name("String"))
      case _ => None
    }
  }

  private def inferRepeated(repeated: Term.Repeated): Option[Type] = {
    infer(repeated.expr)
      .map(tpe => Type.Apply(Type.Name("Array"), args = List(tpe)))
  }
}

object TermTypeInferer extends TermTypeInfererImpl(LitTypeInferer)
