package io.github.effiban.scala2java.core.renderers

import scala.meta.{Term, XtensionQuasiquoteTerm}

trait CompositeApplyTypeRenderer extends JavaTreeRenderer[Term.ApplyType]

private[renderers] class CompositeApplyTypeRendererImpl(classOfRenderer: => ClassOfRenderer,
                                                        standardApplyTypeRenderer: => StandardApplyTypeRenderer)
  extends CompositeApplyTypeRenderer {

  override def render(applyType: Term.ApplyType): Unit = applyType match {
    case anApplyType@Term.ApplyType(q"classOf", _) => classOfRenderer.render(anApplyType)
    case anApplyType => standardApplyTypeRenderer.render(anApplyType)
  }
}