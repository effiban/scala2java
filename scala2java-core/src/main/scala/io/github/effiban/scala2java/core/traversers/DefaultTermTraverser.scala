package io.github.effiban.scala2java.core.traversers

import scala.meta.Term
import scala.meta.Term.{AnonymousFunction, ApplyType, Ascribe, Assign, Block, Do, Eta, If, New, NewAnonymous, Return, Throw, Try, TryWithHandler, While}

trait DefaultTermTraverser extends TermTraverser

private[traversers] class DefaultTermTraverserImpl(defaultTermRefTraverser: => DefaultTermRefTraverser,
                                                   termApplyTraverser: => TermApplyTraverser,
                                                   applyTypeTraverser: => ApplyTypeTraverser,
                                                   termApplyInfixTraverser: => TermApplyInfixTraverser)
  extends DefaultTermTraverser {

  override def traverse(term: Term): Term = term match {
    case termRef: Term.Ref => defaultTermRefTraverser.traverse(termRef)
    case apply: Term.Apply => termApplyTraverser.traverse(apply)
    case applyType: ApplyType => applyTypeTraverser.traverse(applyType)
    case applyInfix: Term.ApplyInfix => termApplyInfixTraverser.traverse(applyInfix)
    case assign: Assign => assign //TODO
    case `return`: Return => `return` //TODO
    case `throw`: Throw => `throw` //TODO
    case ascribe: Ascribe => ascribe //TODO
    case annotate: Term.Annotate => annotate //TODO
    case tuple: Term.Tuple => tuple //TODO
    case block: Block => block //TODO
    case `if`: If => `if` //TODO
    case `match`: Term.Match => `match` //TODO
    case `try`: Try => `try` //TODO
    case tryWithHandler: TryWithHandler => tryWithHandler //TODO
    case `function`: Term.Function => `function` //TODO
    case partialFunction: Term.PartialFunction => partialFunction //TODO
    case anonFunction: AnonymousFunction => anonFunction //TODO
    case `while`: While => `while` //TODO
    case `do`: Do => `do` //TODO
    case `new`: New => `new` //TODO
    case newAnonymous: NewAnonymous => newAnonymous //TODO
    case eta: Eta => eta //TODO
    case termRepeated: Term.Repeated => termRepeated //TODO
    case other => other
  }
}
