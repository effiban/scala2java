package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.entities.TermNameValues.ScalaClassOf

import scala.meta.Term

trait CompositeApplyTypeRenderer extends JavaTreeRenderer[Term.ApplyType]

private[renderers] class CompositeApplyTypeRendererImpl(classOfRenderer: => ClassOfRenderer,
                                                        standardApplyTypeRenderer: => StandardApplyTypeRenderer)
  extends CompositeApplyTypeRenderer {

  override def render(applyType: Term.ApplyType): Unit = applyType match {
    case anApplyType@Term.ApplyType(Term.Name(ScalaClassOf), _) => classOfRenderer.render(anApplyType)
    case anApplyType => standardApplyTypeRenderer.render(anApplyType)
  }
}