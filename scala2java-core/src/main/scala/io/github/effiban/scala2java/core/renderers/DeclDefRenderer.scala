package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.renderers.contexts.{DefRenderContext, ModifiersRenderContext, TermParamListRenderContext}
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.{Decl, Type}

trait DeclDefRenderer {
  def render(declDef: Decl.Def, context: DefRenderContext = DefRenderContext()): Unit
}

private[renderers] class DeclDefRendererImpl(modListRenderer: => ModListRenderer,
                                             typeParamListRenderer: => TypeParamListRenderer,
                                             termNameRenderer: TermNameRenderer,
                                             typeRenderer: => TypeRenderer,
                                             termParamListRenderer: => TermParamListRenderer)
                                            (implicit javaWriter: JavaWriter) extends DeclDefRenderer {

  import javaWriter._

  override def render(declDef: Decl.Def, context: DefRenderContext = DefRenderContext()): Unit = {
    writeLine()
    renderModifiers(declDef, context)
    renderTypeParams(declDef)
    renderMethodType(declDef.decltpe)
    termNameRenderer.render(declDef.name)
    renderMethodParams(declDef)
  }

  private def renderModifiers(declDef: Decl.Def, context: DefRenderContext): Unit = {
    val modifiersRenderContext = ModifiersRenderContext(scalaMods = declDef.mods, javaModifiers = context.javaModifiers)
    modListRenderer.render(modifiersRenderContext)
  }

  private def renderTypeParams(declDef: Decl.Def): Unit = {
    if (declDef.tparams.nonEmpty) {
      typeParamListRenderer.render(declDef.tparams)
      write(" ")
    }
  }

  private def renderMethodType(decltpe: Type): Unit = {
    typeRenderer.render(decltpe)
    write(" ")
  }

  private def renderMethodParams(defDef: Decl.Def): Unit = {
    termParamListRenderer.render(defDef.paramss.flatten, TermParamListRenderContext())
  }
}
