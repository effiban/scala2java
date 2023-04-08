package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Term.{AnonymousFunction, ApplyType, Ascribe, Assign, Block, Do, Eta, For, ForYield, If, New, NewAnonymous, Return, Throw, Try, TryWithHandler, While}
import scala.meta.{Lit, Term}

private[traversers] class DefaultTermTraverser(defaultTermRefTraverser: => TermRefTraverser,
                                               termApplyTraverser: => TermApplyTraverser,
                                               applyTypeTraverser: => ApplyTypeTraverser,
                                               termApplyInfixTraverser: => TermApplyInfixTraverser,
                                               assignTraverser: => AssignTraverser,
                                               returnTraverser: => ReturnTraverser,
                                               throwTraverser: => ThrowTraverser,
                                               ascribeTraverser: => AscribeTraverser,
                                               termAnnotateTraverser: => TermAnnotateTraverser,
                                               termTupleTraverser: => TermTupleTraverser,
                                               blockTraverser: => BlockTraverser,
                                               ifTraverser: => IfTraverser,
                                               termMatchTraverser: => TermMatchTraverser,
                                               tryTraverser: => TryTraverser,
                                               tryWithHandlerTraverser: => TryWithHandlerTraverser,
                                               termFunctionTraverser: => TermFunctionTraverser,
                                               partialFunctionTraverser: => PartialFunctionTraverser,
                                               anonymousFunctionTraverser: => AnonymousFunctionTraverser,
                                               whileTraverser: => WhileTraverser,
                                               doTraverser: => DoTraverser,
                                               forTraverser: => ForTraverser,
                                               forYieldTraverser: => ForYieldTraverser,
                                               newTraverser: => NewTraverser,
                                               newAnonymousTraverser: => NewAnonymousTraverser,
                                               termPlaceholderTraverser: => TermPlaceholderTraverser,
                                               etaTraverser: => EtaTraverser,
                                               termRepeatedTraverser: => TermRepeatedTraverser,
                                               termInterpolateTraverser: => TermInterpolateTraverser,
                                               litTraverser: => LitTraverser)
                                              (implicit javaWriter: JavaWriter) extends TermTraverser {

  import javaWriter._

  override def traverse(term: Term): Unit = term match {
    case termRef: Term.Ref => defaultTermRefTraverser.traverse(termRef)
    case apply: Term.Apply => termApplyTraverser.traverse(apply)
    case applyType: ApplyType => applyTypeTraverser.traverse(applyType)
    case applyInfix: Term.ApplyInfix => termApplyInfixTraverser.traverse(applyInfix)
    case assign: Assign => assignTraverser.traverse(assign)
    case `return`: Return => returnTraverser.traverse(`return`)
    case `throw`: Throw => throwTraverser.traverse(`throw`)
    case ascribe: Ascribe => ascribeTraverser.traverse(ascribe)
    case annotate: Term.Annotate => termAnnotateTraverser.traverse(annotate)
    case tuple: Term.Tuple => termTupleTraverser.traverse(tuple)
    case block: Block => blockTraverser.traverse(block)
    case `if`: If => ifTraverser.traverse(`if`)
    case `match`: Term.Match => termMatchTraverser.traverse(`match`)
    case `try`: Try => tryTraverser.traverse(`try`)
    case tryWithHandler: TryWithHandler => tryWithHandlerTraverser.traverse(tryWithHandler)
    case `function`: Term.Function => termFunctionTraverser.traverse(`function`)
    case partialFunction: Term.PartialFunction => partialFunctionTraverser.traverse(partialFunction)
    case anonFunction: AnonymousFunction => anonymousFunctionTraverser.traverse(anonFunction)
    case `while`: While => whileTraverser.traverse(`while`)
    case `do`: Do => doTraverser.traverse(`do`)
    case `for`: For => forTraverser.traverse(`for`)
    case forYield: ForYield => forYieldTraverser.traverse(forYield)
    case `new`: New => newTraverser.traverse(`new`)
    case newAnonymous: NewAnonymous => newAnonymousTraverser.traverse(newAnonymous)
    case termPlaceholder: Term.Placeholder => termPlaceholderTraverser.traverse(termPlaceholder)
    case eta: Eta => etaTraverser.traverse(eta)
    case termRepeated: Term.Repeated => termRepeatedTraverser.traverse(termRepeated)
    case interpolate: Term.Interpolate => termInterpolateTraverser.traverse(interpolate)
    case literal: Lit => litTraverser.traverse(literal)
    case _ => writeComment(s"UNSUPPORTED: $term")
  }
}
