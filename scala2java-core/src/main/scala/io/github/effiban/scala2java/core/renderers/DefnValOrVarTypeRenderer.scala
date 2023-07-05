package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.contexts.ValOrVarRenderContext
import io.github.effiban.scala2java.core.entities.TraversalConstants.UnknownType
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Type

trait DefnValOrVarTypeRenderer {
  def render(maybeDeclType: Option[Type],
             context: ValOrVarRenderContext = ValOrVarRenderContext()): Unit
}

private[renderers] class DefnValOrVarTypeRendererImpl(typeRenderer: => TypeRenderer)
                                                     (implicit javaWriter: JavaWriter) extends DefnValOrVarTypeRenderer {

  import javaWriter._

  override def render(maybeDeclType: Option[Type],
                      context: ValOrVarRenderContext = ValOrVarRenderContext()): Unit = {
    maybeDeclType match {
      case Some(declType) => typeRenderer.render(declType)
      // TODO write 'var' also if type is parameterized (Type.Apply)
      case None if context.inBlock => write("var")
      case _ => handleUnknownType()
    }
  }

  private def handleUnknownType(): Unit = writeComment(UnknownType)
}
