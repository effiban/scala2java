package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.renderers.contexts.{CtorSecondaryRenderContext, InitRenderContext, ModifiersRenderContext}
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Ctor

trait CtorSecondaryRenderer {
  def render(secondaryCtor: Ctor.Secondary, context: CtorSecondaryRenderContext): Unit
}

private[renderers] class CtorSecondaryRendererImpl(modListRenderer: => ModListRenderer,
                                                   typeNameRenderer: => TypeNameRenderer,
                                                   termParamListRenderer: => TermParamListRenderer,
                                                   initRenderer: => InitRenderer,
                                                   blockStatRenderer: => BlockStatRenderer)
                                                  (implicit javaWriter: JavaWriter)
  extends CtorSecondaryRenderer {

  import javaWriter._

  override def render(secondaryCtor: Ctor.Secondary, context: CtorSecondaryRenderContext): Unit = {
    writeLine()
    renderMods(secondaryCtor, context)
    renderClassName(context)
    renderParams(secondaryCtor)
    renderBody(secondaryCtor)
  }

  private def renderMods(secondaryCtor: Ctor.Secondary, context: CtorSecondaryRenderContext): Unit = {
    val modifiersRenderContext = ModifiersRenderContext(scalaMods = secondaryCtor.mods, javaModifiers = context.javaModifiers)
    modListRenderer.render(modifiersRenderContext)
  }

  private def renderClassName(context: CtorSecondaryRenderContext): Unit = {
    typeNameRenderer.render(context.className)
  }

  private def renderParams(secondaryCtor: Ctor.Secondary): Unit = {
    termParamListRenderer.render(secondaryCtor.paramss.flatten)
  }

  private def renderBody(secondaryCtor: Ctor.Secondary): Unit = {
    writeBlockStart()
    initRenderer.render(secondaryCtor.init, InitRenderContext(argNameAsComment = true))
    writeStatementEnd()
    secondaryCtor.stats.foreach(blockStatRenderer.render)
    writeBlockEnd()
  }
}
