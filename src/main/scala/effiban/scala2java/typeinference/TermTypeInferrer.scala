package effiban.scala2java.typeinference

import scala.meta.Term.{Annotate, Ascribe, Assign, Block, Do, For, ForYield, If, New, Return, Throw, Try, TryWithHandler, While}
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
      case partialFunction: Term.PartialFunction => caseListTypeInferrer.infer(partialFunction.cases)
      case repeated: Term.Repeated => inferRepeated(repeated)
      case forYield: ForYield => infer(forYield.body)
      case `return`: Return => infer(`return`.expr)
      case assign: Assign => infer(assign.rhs)
      case function: Term.Function => infer(function.body)
      case ascribe: Ascribe => Some(ascribe.tpe)
      case `new`: New => Some(`new`.init.tpe)
      case _: Term.Interpolate => Some(Type.Name("String"))
      case _: For => Some(Type.AnonymousName())
      case _: Annotate => Some(Type.AnonymousName())
      case _: Do => Some(Type.AnonymousName())
      case _: While => Some(Type.AnonymousName())
      case _: Throw => Some(Type.AnonymousName())
      // TODO - support Tuple
      case _ => None
    }
  }

  private def inferRepeated(repeated: Term.Repeated): Option[Type] = {
    infer(repeated.expr)
      .map(tpe => Type.Apply(Type.Name("Array"), args = List(tpe)))
  }
}
