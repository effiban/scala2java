package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.entities.TraversalConstants.UnknownType
import io.github.effiban.scala2java.core.renderers.contexts.{BlockRenderContext, DefnDefRenderContext, ModifiersRenderContext, TermParamListRenderContext}
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.{Defn, Type}

trait DefnDefRenderer {
  def render(defnDef: Defn.Def, context: DefnDefRenderContext = DefnDefRenderContext()): Unit
}

private[renderers] class DefnDefRendererImpl(modListRenderer: => ModListRenderer,
                                             typeParamListRenderer: => TypeParamListRenderer,
                                             termNameRenderer: TermNameRenderer,
                                             typeRenderer: => TypeRenderer,
                                             termParamListRenderer: => TermParamListRenderer,
                                             blockCoercingTermRenderer: => BlockCoercingTermRenderer)
                                            (implicit javaWriter: JavaWriter) extends DefnDefRenderer {

  import javaWriter._

  override def render(defnDef: Defn.Def, context: DefnDefRenderContext = DefnDefRenderContext()): Unit = {
    writeLine()
    renderModifiers(defnDef, context)
    renderTypeParams(defnDef)
    renderMethodType(defnDef.decltpe)
    termNameRenderer.render(defnDef.name)
    renderMethodParamsAndBody(defnDef)
  }

  private def renderModifiers(defnDef: Defn.Def, context: DefnDefRenderContext): Unit = {
    val modifiersRenderContext = ModifiersRenderContext(scalaMods = defnDef.mods, javaModifiers = context.javaModifiers)
    modListRenderer.render(modifiersRenderContext)
  }

  private def renderTypeParams(defnDef: Defn.Def): Unit = {
    if (defnDef.tparams.nonEmpty) {
      typeParamListRenderer.render(defnDef.tparams)
      write(" ")
    }
  }

  private def renderMethodType(maybeType: Option[Type]): Unit = {
    maybeType match {
      case Some(Type.AnonymousName()) =>
      case Some(tpe) =>
        typeRenderer.render(tpe)
        write(" ")
      case None =>
        writeComment(UnknownType)
        write(" ")
    }
  }

  private def renderMethodParamsAndBody(defDef: Defn.Def): Unit = {
    termParamListRenderer.render(defDef.paramss.flatten, TermParamListRenderContext())
    blockCoercingTermRenderer.render(defDef.body, BlockRenderContext(uncertainReturn = defDef.decltpe.isEmpty))
  }
}
