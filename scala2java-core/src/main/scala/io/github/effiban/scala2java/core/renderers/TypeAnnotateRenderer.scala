package io.github.effiban.scala2java.core.renderers

import scala.meta.Type

trait TypeAnnotateRenderer extends JavaTreeRenderer[Type.Annotate]

private[renderers] class TypeAnnotateRendererImpl(typeRenderer: => TypeRenderer) extends TypeAnnotateRenderer {

  // type with annotation, e.g.: T @annot
  override def render(annotatedType: Type.Annotate): Unit = {
    //TODO - uncomment after renderers have been extracted
    /**annotListRenderer.renderAnnotations(annotations = annotatedType.annots, onSameLine = true)
    write(" ")*/
    typeRenderer.render(annotatedType.tpe)
  }
}
