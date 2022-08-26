package effiban.scala2java.typeinference

import scala.meta.Term.{Ascribe, Block, ForYield, If, New, Return, Try, TryWithHandler}
import scala.meta.{Lit, Term, Type}

trait TermTypeInferrer extends TypeInferrer[Term]

private[typeinference] class TermTypeInferrerImpl(ifTypeInferrer: => IfTypeInferrer,
                                                  blockTypeInferrer: => BlockTypeInferrer,
                                                  litTypeInferrer: LitTypeInferrer,
                                                  tryTypeInferrer: => TryTypeInferrer,
                                                  tryWithHandlerTypeInferrer: => TryWithHandlerTypeInferrer,
                                                  caseListTypeInferrer: => CaseListTypeInferrer) extends TermTypeInferrer {

  override def infer(term: Term): Option[Type] = {
    term match {
      case `if`: If => ifTypeInferrer.infer(`if`)
      case block: Block => blockTypeInferrer.infer(block)
      case lit: Lit => litTypeInferrer.infer(lit)
      case `try`: Try => tryTypeInferrer.infer(`try`)
      case tryWithHandler: TryWithHandler => tryWithHandlerTypeInferrer.infer(tryWithHandler)
      case termMatch: Term.Match => caseListTypeInferrer.infer(termMatch.cases)
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
