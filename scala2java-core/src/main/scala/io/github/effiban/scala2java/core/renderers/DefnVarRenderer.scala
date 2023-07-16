package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.renderers.contexts.{ModifiersRenderContext, VarRenderContext}
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Defn

trait DefnVarRenderer {
  def render(varDef: Defn.Var, context: VarRenderContext = VarRenderContext()): Unit
}

private[renderers] class DefnVarRendererImpl(modListRenderer: => ModListRenderer,
                                             defnVarTypeRenderer: => DefnVarTypeRenderer,
                                             patListRenderer: => PatListRenderer,
                                             expressionTermRenderer: => ExpressionTermRenderer)
                                            (implicit javaWriter: JavaWriter) extends DefnVarRenderer {

  import javaWriter._

  override def render(varDef: Defn.Var, context: VarRenderContext = VarRenderContext()): Unit = {
    val modifiersRenderContext = ModifiersRenderContext(
      scalaMods = varDef.mods,
      javaModifiers = context.javaModifiers
    )
    modListRenderer.render(modifiersRenderContext)
    defnVarTypeRenderer.render(varDef.decltpe, context)
    write(" ")
    //TODO verify for non-simple cases
    patListRenderer.render(varDef.pats)
    varDef.rhs.foreach { rhs =>
      write(" = ")
      expressionTermRenderer.render(rhs)
    }
  }
}
