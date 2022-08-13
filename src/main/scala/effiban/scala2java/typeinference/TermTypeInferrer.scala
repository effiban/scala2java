package effiban.scala2java.typeinference

import scala.meta.Term.{Ascribe, New, Return}
import scala.meta.{Lit, Term, Type}

trait TermTypeInferrer extends TypeInferrer[Term]

private[typeinference] class TermTypeInferrerImpl(litTypeInferrer: LitTypeInferrer) extends TermTypeInferrer {

  override def infer(term: Term): Option[Type] = {
    term match {
      case lit: Lit => litTypeInferrer.infer(lit)
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

object TermTypeInferrer extends TermTypeInferrerImpl(LitTypeInferrer)
