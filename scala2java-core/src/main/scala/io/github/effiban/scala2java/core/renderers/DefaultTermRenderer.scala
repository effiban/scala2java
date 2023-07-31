package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Term.{ApplyType, Ascribe, Assign, Block, Do, Eta, If, New, NewAnonymous, Return, Throw, Try, TryWithHandler, While}
import scala.meta.{Lit, Term}

trait DefaultTermRenderer extends TermRenderer

private[renderers] class DefaultTermRendererImpl(defaultTermRefRenderer: => DefaultTermRefRenderer,
                                                 applyRenderer: => TermApplyRenderer,
                                                 compositeApplyTypeRenderer: => CompositeApplyTypeRenderer,
                                                 applyInfixRenderer: => TermApplyInfixRenderer,
                                                 assignRenderer: => AssignRenderer,
                                                 returnRenderer: => ReturnRenderer,
                                                 throwRenderer: => ThrowRenderer,
                                                 ascribeRenderer: => AscribeRenderer,
                                                 termAnnotateRenderer: => TermAnnotateRenderer,
                                                 blockRenderer: => BlockRenderer,
                                                 ifRenderer: => IfRenderer,
                                                 matchRenderer: => TermMatchRenderer,
                                                 tryRenderer: => TryRenderer,
                                                 tryWithHandlerRenderer: => TryWithHandlerRenderer,
                                                 termFunctionRenderer: => TermFunctionRenderer,
                                                 whileRenderer: => WhileRenderer,
                                                 doRenderer: => DoRenderer,
                                                 newRenderer: => NewRenderer,
                                                 newAnonymousRenderer: => NewAnonymousRenderer,
                                                 termPlaceholderRenderer: => TermPlaceholderRenderer,
                                                 etaRenderer: => EtaRenderer,
                                                 litRenderer: LitRenderer)
                                                (implicit javaWriter: JavaWriter) extends DefaultTermRenderer {

  import javaWriter._

  override def render(term: Term): Unit = term match {
    case termRef: Term.Ref => defaultTermRefRenderer.render(termRef)
    case apply: Term.Apply => applyRenderer.render(apply)
    case applyType: ApplyType => compositeApplyTypeRenderer.render(applyType)
    case applyInfix: Term.ApplyInfix => applyInfixRenderer.render(applyInfix)
    case assign: Assign => assignRenderer.render(assign)
    case `return`: Return => returnRenderer.render(`return`)
    case `throw`: Throw => throwRenderer.render(`throw`)
    case ascribe: Ascribe => ascribeRenderer.render(ascribe)
    case annotate: Term.Annotate => termAnnotateRenderer.render(annotate)
    case block: Block => blockRenderer.render(block)
    case `if`: If => ifRenderer.render(`if`)
    case `match`: Term.Match => matchRenderer.render(`match`)
    case `try`: Try => tryRenderer.render(`try`)
    case tryWithHandler: TryWithHandler => tryWithHandlerRenderer.render(tryWithHandler)
    case `function`: Term.Function => termFunctionRenderer.render(`function`)
    case `while`: While => whileRenderer.render(`while`)
    case `do`: Do => doRenderer.render(`do`)
    case `new`: New => newRenderer.render(`new`)
    case newAnonymous: NewAnonymous => newAnonymousRenderer.render(newAnonymous)
    case termPlaceholder: Term.Placeholder => termPlaceholderRenderer.render(termPlaceholder)
    case eta: Eta => etaRenderer.render(eta)
    case literal: Lit => litRenderer.render(literal)
    case _ => writeComment(s"UNSUPPORTED: $term")
  }
}
