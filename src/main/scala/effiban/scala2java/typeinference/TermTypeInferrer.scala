package effiban.scala2java.typeinference

import scala.meta.Term.{Ascribe, Block, ForYield, If, New, Return}
import scala.meta.{Lit, Term, Type}

trait TermTypeInferrer extends TypeInferrer[Term]

private[typeinference] class TermTypeInferrerImpl(ifTypeInferrer: => IfTypeInferrer,
                                                  blockTypeInferrer: => BlockTypeInferrer,
                                                  litTypeInferrer: LitTypeInferrer) extends TermTypeInferrer {

  override def infer(term: Term): Option[Type] = {
    term match {
      case `if`: If => ifTypeInferrer.infer(`if`)
      case block: Block => blockTypeInferrer.infer(block)
      case lit: Lit => litTypeInferrer.infer(lit)
      case repeated: Term.Repeated => inferRepeated(repeated)
      case forYield: ForYield => infer(forYield.body)
      case `return`: Return => infer(`return`.expr)
      case ascribe: Ascribe => Some(ascribe.tpe)
      case `new`: New => Some(`new`.init.tpe)
      case _: Term.Interpolate => Some(Type.Name("String"))
      case _ => None
    }
  }

  private def inferRepeated(repeated: Term.Repeated): Option[Type] = {
    infer(repeated.expr)
      .map(tpe => Type.Apply(Type.Name("Array"), args = List(tpe)))
  }
}
