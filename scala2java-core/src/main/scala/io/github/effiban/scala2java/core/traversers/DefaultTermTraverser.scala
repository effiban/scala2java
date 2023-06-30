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
                                                   anonymousFunctionTraverser: => AnonymousFunctionTraverser)
  extends DefaultTermTraverser {

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
    case `if`: If => defaultIfTraverser.traverse(`if`).stat
    case `match`: Term.Match => termMatchTraverser.traverse(`match`)
    case `try`: Try => tryTraverser.traverse(`try`).stat
    case tryWithHandler: TryWithHandler => tryWithHandlerTraverser.traverse(tryWithHandler).stat
    case `function`: Term.Function => termFunctionTraverser.traverse(`function`).function
    case partialFunction: Term.PartialFunction => partialFunction //TODO
    case anonFunction: AnonymousFunction => anonymousFunctionTraverser.traverse(anonFunction).function
    case `while`: While => `while` //TODO
    case `do`: Do => `do` //TODO
    case `new`: New => `new` //TODO
    case newAnonymous: NewAnonymous => newAnonymous //TODO
    case eta: Eta => eta //TODO
    case termRepeated: Term.Repeated => termRepeated //TODO
    case other => other
  }
}
