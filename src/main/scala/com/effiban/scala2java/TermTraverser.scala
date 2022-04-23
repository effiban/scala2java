package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emitComment

import scala.meta.Term.{AnonymousFunction, ApplyType, Ascribe, Assign, Block, Do, Eta, For, ForYield, If, New, NewAnonymous, Return, Throw, Try, TryWithHandler, While}
import scala.meta.{Lit, Term}

object TermTraverser extends ScalaTreeTraverser[Term] {

  override def traverse(term: Term): Unit = term match {
    case apply: Term.Apply => TermApplyTraverser.traverse(apply)
    case applyType: ApplyType => ApplyTypeTraverser.traverse(applyType)
    case applyInfix: Term.ApplyInfix => TermApplyInfixTraverser.traverse(applyInfix)
    case assign: Assign => AssignTraverser.traverse(assign)
    case `return`: Return => ReturnTraverser.traverse(`return`)
    case `throw`: Throw => ThrowTraverser.traverse(`throw`)
    case ascribe: Ascribe => AscribeTraverser.traverse(ascribe)
    case annotate: Term.Annotate => TermAnnotateTraverser.traverse(annotate)
    case tuple: Term.Tuple => TermTupleTraverser.traverse(tuple)
    case block: Block => BlockTraverser.traverse(block)
    case `if`: If => IfTraverser.traverse(`if`)
    case `match`: Term.Match => TermMatchTraverser.traverse(`match`)
    case `try`: Try => TryTraverser.traverse(`try`)
    case tryWithHandler: TryWithHandler => TryWithHandlerTraverser.traverse(tryWithHandler)
    case `function`: Term.Function => TermFunctionTraverser.traverse(`function`)
    case partialFunction: Term.PartialFunction => PartialFunctionTraverser.traverse(partialFunction)
    case anonFunction: AnonymousFunction => AnonymousFunctionTraverser.traverse(anonFunction)
    case `while`: While => WhileTraverser.traverse(`while`)
    case `do`: Do => DoTraverser.traverse(`do`)
    case `for`: For => ForTraverser.traverse(`for`)
    case forYield: ForYield => ForYieldTraverser.traverse(forYield)
    case `new`: New => NewTraverser.traverse(`new`)
    case newAnonymous: NewAnonymous => NewAnonymousTraverser.traverse(newAnonymous)
    case termPlaceholder: Term.Placeholder => TermPlaceholderTraverser.traverse(termPlaceholder)
    case eta: Eta => EtaTraverser.traverse(eta)
    case termRepeated: Term.Repeated => TermRepeatedTraverser.traverse(termRepeated)
    case interpolate: Term.Interpolate => TermInterpolateTraverser.traverse(interpolate)
    case xml: Term.Xml => TermXmlTraverser.traverse(xml)
    case literal: Lit => LitTraverser.traverse(literal)
    case _ => emitComment(s"UNSUPPORTED: $term")
  }
}
