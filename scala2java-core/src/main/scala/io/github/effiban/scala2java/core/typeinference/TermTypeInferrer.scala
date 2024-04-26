package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.spi.typeinferrers.TypeInferrer0

import scala.meta.Term.{Annotate, ApplyType, Ascribe, Assign, Block, Do, For, ForYield, If, New, Return, Throw, Try, TryWithHandler, While}
import scala.meta.{Lit, Term, Type}

trait TermTypeInferrer extends TypeInferrer0[Term]

private[typeinference] class TermTypeInferrerImpl(applyInfixTypeInferrer: => ApplyInfixTypeInferrer,
                                                  applyReturnTypeInferrer: => ApplyReturnTypeInferrer,
                                                  applyTypeTypeInferrer: => ApplyTypeTypeInferrer,
                                                  blockTypeInferrer: => BlockTypeInferrer,
                                                  caseListTypeInferrer: => CaseListTypeInferrer,
                                                  functionTypeInferrer: => FunctionTypeInferrer,
                                                  ifTypeInferrer: => IfTypeInferrer,
                                                  litTypeInferrer: LitTypeInferrer,
                                                  selectTypeInferrer: => InternalSelectTypeInferrer,
                                                  tryTypeInferrer: => TryTypeInferrer,
                                                  tryWithHandlerTypeInferrer: => TryWithHandlerTypeInferrer,
                                                  tupleTypeInferrer: => TupleTypeInferrer) extends TermTypeInferrer {

  override def infer(term: Term): Option[Type] = {
    term match {
      case _: Annotate => Some(Type.AnonymousName())
      case apply: Term.Apply => applyReturnTypeInferrer.infer(apply)
      case applyInfix: Term.ApplyInfix => applyInfixTypeInferrer.infer(applyInfix)
      case applyType: ApplyType => applyTypeTypeInferrer.infer(applyType)
      case ascribe: Ascribe => Some(ascribe.tpe)
      case assign: Assign => infer(assign.rhs)
      case block: Block => blockTypeInferrer.infer(block)
      case _: Do => Some(Type.AnonymousName())
      case _: For => Some(Type.AnonymousName())
      case forYield: ForYield => infer(forYield.body)
      case function: Term.Function => Some(functionTypeInferrer.infer(function))
      case `if`: If => ifTypeInferrer.infer(`if`)
      case _: Term.Interpolate => Some(Type.Name("String"))
      case lit: Lit => litTypeInferrer.infer(lit)
      case _: Term.Name => None // TODO handle local/file/package-scope Term.Name-s
      case `new`: New => Some(`new`.init.tpe)
      case repeated: Term.Repeated => inferRepeated(repeated)
      case `return`: Return => infer(`return`.expr)
      case select: Term.Select => selectTypeInferrer.infer(select)
      case termMatch: Term.Match => caseListTypeInferrer.infer(termMatch.cases)
      case _: Throw => Some(Type.AnonymousName())
      case `try`: Try => tryTypeInferrer.infer(`try`)
      case tryWithHandler: TryWithHandler => tryWithHandlerTypeInferrer.infer(tryWithHandler)
      case tuple: Term.Tuple => Some(tupleTypeInferrer.infer(tuple))
      case _: While => Some(Type.AnonymousName())
      // TODO - support NewAnonymous, PartialFunction
      case _ => None
    }
  }

  private def inferRepeated(repeated: Term.Repeated): Option[Type] = {
    infer(repeated.expr)
      .map(tpe => Type.Apply(Type.Name("Array"), args = List(tpe)))
  }
}
