package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.entities.TermNameValues.ScalaClassOf
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Term

trait ApplyTypeRenderer extends JavaTreeRenderer[Term.ApplyType]

private[renderers] class ApplyTypeRendererImpl(classOfRenderer: => ClassOfRenderer)
                                              (implicit javaWriter: JavaWriter) extends ApplyTypeRenderer {

  import javaWriter._

  override def render(applyType: Term.ApplyType): Unit = applyType match {
    case anApplyType@Term.ApplyType(Term.Name(ScalaClassOf), _) => classOfRenderer.render(anApplyType)
    case anApplyType => write(s"UNSUPPORTED: $anApplyType")
  }
}