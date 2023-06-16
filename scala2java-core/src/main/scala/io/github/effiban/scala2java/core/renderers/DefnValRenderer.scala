package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.contexts.{ModifiersRenderContext, ValOrVarRenderContext}
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Defn

trait DefnValRenderer {
  def render(valDef: Defn.Val, context: ValOrVarRenderContext = ValOrVarRenderContext()): Unit
}

private[renderers] class DefnValRendererImpl(modListRenderer: => ModListRenderer,
                                             defnValOrVarTypeRenderer: => DefnValOrVarTypeRenderer,
                                             patListRenderer: => PatListRenderer,
                                             expressionTermRenderer: => ExpressionTermRenderer)
                                            (implicit javaWriter: JavaWriter) extends DefnValRenderer {

  import javaWriter._

  override def render(valDef: Defn.Val, context: ValOrVarRenderContext = ValOrVarRenderContext()): Unit = {
    modListRenderer.render(ModifiersRenderContext(
      scalaMods = valDef.mods,
      javaModifiers = context.javaModifiers
    ))
    defnValOrVarTypeRenderer.render(valDef.decltpe, context)
    write(" ")
    //TODO verify for non-simple cases
    patListRenderer.render(valDef.pats)
    write(" = ")
    expressionTermRenderer.render(valDef.rhs)
  }
}
