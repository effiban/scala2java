package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.contexts.{ModifiersRenderContext, ValOrVarRenderContext}
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Decl

trait DeclVarRenderer {
  def render(varDecl: Decl.Var, context: ValOrVarRenderContext = ValOrVarRenderContext()): Unit
}

private[renderers] class DeclVarRendererImpl(modListRenderer: => ModListRenderer,
                                             typeRenderer: => TypeRenderer,
                                             patListRenderer: => PatListRenderer)
                                            (implicit javaWriter: JavaWriter) extends DeclVarRenderer {

  import javaWriter._

  override def render(varDecl: Decl.Var, context: ValOrVarRenderContext = ValOrVarRenderContext()): Unit = {
    val modifiersRenderContext = ModifiersRenderContext(
      scalaMods = varDecl.mods,
      javaModifiers = context.javaModifiers
    )
    modListRenderer.render(modifiersRenderContext)
    typeRenderer.render(varDecl.decltpe)
    write(" ")
    //TODO verify for non-simple cases
    patListRenderer.render(varDecl.pats)
  }
}
