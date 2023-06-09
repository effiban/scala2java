package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Term.{AnonymousFunction, ApplyType, Ascribe, Assign, Block, Do, Eta, If, New, NewAnonymous, Return, Throw, Try, TryWithHandler, While}
import scala.meta.{Lit, Term}

trait DefaultTermRenderer extends TermRenderer

private[renderers] class DefaultTermRendererImpl(defaultTermRefRenderer: => DefaultTermRefRenderer,
                                                 applyRenderer: => TermApplyRenderer,
                                                 applyTypeRenderer: => ApplyTypeRenderer,
                                                 applyInfixRenderer: => TermApplyInfixRenderer,
                                                 assignRenderer: => AssignRenderer,
                                                 returnRenderer: => ReturnRenderer,
                                                 blockRenderer: => BlockRenderer,
                                                 ifRenderer: => IfRenderer,
                                                 litRenderer: LitRenderer)
                                                (implicit javaWriter: JavaWriter) extends DefaultTermRenderer {

  import javaWriter._

  override def render(term: Term): Unit = term match {
    case termRef: Term.Ref => defaultTermRefRenderer.render(termRef)
    case apply: Term.Apply => applyRenderer.render(apply)
    case applyType: ApplyType => applyTypeRenderer.render(applyType)
    case applyInfix: Term.ApplyInfix => applyInfixRenderer.render(applyInfix)
    case assign: Assign => assignRenderer.render(assign)
    case `return`: Return => returnRenderer.render(`return`)
    case `throw`: Throw => //TODO
    case ascribe: Ascribe => //TODO
    case annotate: Term.Annotate => //TODO
    case tuple: Term.Tuple => //TODO
    case block: Block => blockRenderer.render(block)
    case `if`: If => ifRenderer.render(`if`)
    case `match`: Term.Match => //TODO
    case `try`: Try => //TODO
    case tryWithHandler: TryWithHandler => //TODO
    case `function`: Term.Function => //TODO
    case partialFunction: Term.PartialFunction => //TODO
    case anonFunction: AnonymousFunction => //TODO
    case `while`: While => //TODO
    case `do`: Do => //TODO
    case `new`: New => //TODO
    case newAnonymous: NewAnonymous => //TODO
    case termPlaceholder: Term.Placeholder => //TODO
    case eta: Eta => //TODO
    case termRepeated: Term.Repeated => //TODO
    case literal: Lit => litRenderer.render(literal)
    case _ => writeComment(s"UNSUPPORTED: $term")
  }
}
