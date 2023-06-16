package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.{DefaultTermRenderer, TermPlaceholderRenderer}
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Term.{AnonymousFunction, ApplyType, Ascribe, Assign, Block, Do, Eta, If, New, NewAnonymous, Return, Throw, Try, TryWithHandler, While}
import scala.meta.{Lit, Term}

trait DefaultTermTraverser extends TermTraverser

private[traversers] class DefaultTermTraverserImpl(defaultTermRefTraverser: => DefaultTermRefTraverser,
                                                   termApplyTraverser: => TermApplyTraverser,
                                                   mainApplyTypeTraverser: => MainApplyTypeTraverser,
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
                                                   newTraverser: => NewTraverser,
                                                   newAnonymousTraverser: => NewAnonymousTraverser,
                                                   termPlaceholderRenderer: => TermPlaceholderRenderer,
                                                   etaTraverser: => EtaTraverser,
                                                   termRepeatedTraverser: => TermRepeatedTraverser,
                                                   defaultTermRenderer: => DefaultTermRenderer)
                                                  (implicit javaWriter: JavaWriter) extends DefaultTermTraverser {

  import javaWriter._

  override def traverse(term: Term): Unit = term match {
    case termRef: Term.Ref =>
      val traversedTermRef = defaultTermRefTraverser.traverse(termRef)
      defaultTermRenderer.render(traversedTermRef)
    case apply: Term.Apply => termApplyTraverser.traverse(apply)
    case applyType: ApplyType => mainApplyTypeTraverser.traverse(applyType)
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
    case `new`: New => newTraverser.traverse(`new`)
    case newAnonymous: NewAnonymous => newAnonymousTraverser.traverse(newAnonymous)
    case termPlaceholder: Term.Placeholder =>
      termPlaceholderRenderer.render(termPlaceholder)
    case eta: Eta => etaTraverser.traverse(eta)
    case termRepeated: Term.Repeated => termRepeatedTraverser.traverse(termRepeated)
    case literal: Lit =>
      defaultTermRenderer.render(literal)
    case _ => writeComment(s"UNSUPPORTED: $term")
  }
}
