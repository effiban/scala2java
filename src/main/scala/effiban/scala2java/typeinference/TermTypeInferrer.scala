package effiban.scala2java.typeinference

import scala.meta.Term.{Annotate, ApplyType, Ascribe, Assign, Block, Do, For, ForYield, If, New, Return, Throw, Try, TryWithHandler, While}
import scala.meta.{Lit, Term, Type}

trait TermTypeInferrer extends TypeInferrer[Term]

private[typeinference] class TermTypeInferrerImpl(applyTypeTypeInferrer: ApplyTypeTypeInferrer,
                                                  blockTypeInferrer: => BlockTypeInferrer,
                                                  caseListTypeInferrer: => CaseListTypeInferrer,
                                                  ifTypeInferrer: => IfTypeInferrer,
                                                  litTypeInferrer: LitTypeInferrer,
                                                  tryTypeInferrer: => TryTypeInferrer,
                                                  tryWithHandlerTypeInferrer: => TryWithHandlerTypeInferrer) extends TermTypeInferrer {

  override def infer(term: Term): Option[Type] = {
    term match {
      case _: Annotate => Some(Type.AnonymousName())
      case applyType: ApplyType => applyTypeTypeInferrer.infer(applyType)
      case ascribe: Ascribe => Some(ascribe.tpe)
      case assign: Assign => infer(assign.rhs)
      case block: Block => blockTypeInferrer.infer(block)
      case _: Do => Some(Type.AnonymousName())
      case _: For => Some(Type.AnonymousName())
      case forYield: ForYield => infer(forYield.body)
      case `if`: If => ifTypeInferrer.infer(`if`)
      case _: Term.Interpolate => Some(Type.Name("String"))
      case lit: Lit => litTypeInferrer.infer(lit)
      case `new`: New => Some(`new`.init.tpe)
      case repeated: Term.Repeated => inferRepeated(repeated)
      case `return`: Return => infer(`return`.expr)
      case termMatch: Term.Match => caseListTypeInferrer.infer(termMatch.cases)
      case _: Throw => Some(Type.AnonymousName())
      case `try`: Try => tryTypeInferrer.infer(`try`)
      case tryWithHandler: TryWithHandler => tryWithHandlerTypeInferrer.infer(tryWithHandler)
      case _: While => Some(Type.AnonymousName())
      // TODO - support Tuple, NewAnonymous, Function, PartialFunction
      case _ => None
    }
  }

  private def inferRepeated(repeated: Term.Repeated): Option[Type] = {
    infer(repeated.expr)
      .map(tpe => Type.Apply(Type.Name("Array"), args = List(tpe)))
  }
}
