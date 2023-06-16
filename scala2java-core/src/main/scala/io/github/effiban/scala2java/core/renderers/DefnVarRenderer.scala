package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.contexts.{ModifiersRenderContext, ValOrVarRenderContext}
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Defn

trait DefnVarRenderer {
  def render(varDef: Defn.Var, context: ValOrVarRenderContext = ValOrVarRenderContext()): Unit
}

private[renderers] class DefnVarRendererImpl(modListRenderer: => ModListRenderer,
                                             defnValOrVarTypeRenderer: => DefnValOrVarTypeRenderer,
                                             patListRenderer: => PatListRenderer,
                                             expressionTermRenderer: => ExpressionTermRenderer)
                                            (implicit javaWriter: JavaWriter) extends DefnVarRenderer {

  import javaWriter._

  override def render(varDef: Defn.Var, context: ValOrVarRenderContext = ValOrVarRenderContext()): Unit = {
    val modifiersRenderContext = ModifiersRenderContext(
      scalaMods = varDef.mods,
      javaModifiers = context.javaModifiers
    )
    modListRenderer.render(modifiersRenderContext)
    defnValOrVarTypeRenderer.render(varDef.decltpe, context)
    write(" ")
    //TODO verify for non-simple cases
    patListRenderer.render(varDef.pats)
    varDef.rhs.foreach { rhs =>
      write(" = ")
      expressionTermRenderer.render(rhs)
    }
  }
}
