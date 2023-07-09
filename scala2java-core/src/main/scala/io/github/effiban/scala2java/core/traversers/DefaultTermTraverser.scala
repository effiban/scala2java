package io.github.effiban.scala2java.core.traversers

import scala.meta.Term
import scala.meta.Term.{AnonymousFunction, ApplyType, Ascribe, Assign, Block, Do, Eta, If, New, NewAnonymous, Return, Throw, Try, TryWithHandler, While}

trait DefaultTermTraverser extends TermTraverser

private[traversers] class DefaultTermTraverserImpl(defaultTermRefTraverser: => DefaultTermRefTraverser,
                                                   termApplyTraverser: => TermApplyTraverser,
                                                   applyTypeTraverser: => ApplyTypeTraverser,
                                                   termApplyInfixTraverser: => TermApplyInfixTraverser,
                                                   assignTraverser: => AssignTraverser,
                                                   returnTraverser: => ReturnTraverser,
                                                   throwTraverser: => ThrowTraverser,
                                                   ascribeTraverser: => AscribeTraverser,
                                                   termAnnotateTraverser: => TermAnnotateTraverser,
                                                   termTupleTraverser: => TermTupleTraverser,
                                                   defaultBlockTraverser: => DefaultBlockTraverser,
                                                   defaultIfTraverser: => DefaultIfTraverser,
                                                   termMatchTraverser: => TermMatchTraverser,
                                                   tryTraverser: => TryTraverser,
                                                   tryWithHandlerTraverser: => TryWithHandlerTraverser,
                                                   termFunctionTraverser: => TermFunctionTraverser,
                                                   partialFunctionTraverser: => PartialFunctionTraverser,
                                                   anonymousFunctionTraverser: => AnonymousFunctionTraverser,
                                                   whileTraverser: => WhileTraverser,
                                                   doTraverser: => DoTraverser,
                                                   newTraverser: => NewTraverser,
                                                   etaTraverser: => EtaTraverser,
                                                   termRepeatedTraverser: => TermRepeatedTraverser) extends DefaultTermTraverser {

  override def traverse(term: Term): Term = term match {
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
    case block: Block => defaultBlockTraverser.traverse(block).block
    case `if`: If => defaultIfTraverser.traverse(`if`)
    case `match`: Term.Match => termMatchTraverser.traverse(`match`)
    case `try`: Try => tryTraverser.traverse(`try`)
    case tryWithHandler: TryWithHandler => tryWithHandlerTraverser.traverse(tryWithHandler)
    case `function`: Term.Function => termFunctionTraverser.traverse(`function`)
    case partialFunction: Term.PartialFunction => partialFunctionTraverser.traverse(partialFunction)
    case anonFunction: AnonymousFunction => anonymousFunctionTraverser.traverse(anonFunction)
    case `while`: While => whileTraverser.traverse(`while`)
    case `do`: Do => doTraverser.traverse(`do`)
    case `new`: New => newTraverser.traverse(`new`)
    case newAnonymous: NewAnonymous => newAnonymous //TODO once TemplateTraverser is ready
    case eta: Eta => etaTraverser.traverse(eta)
    case termRepeated: Term.Repeated => termRepeatedTraverser.traverse(termRepeated)
    case other => other
  }
}
