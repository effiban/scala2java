package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.orderings.JavaModifierOrdering
import io.github.effiban.scala2java.core.renderers.contexts.ModifiersRenderContext
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Mod.{Annot, Implicit}

trait ModListRenderer {
  def render(context: ModifiersRenderContext): Unit
}

class ModListRendererImpl(annotListRenderer: => AnnotListRenderer,
                          javaModifierOrdering: JavaModifierOrdering)
                          (implicit javaWriter: JavaWriter) extends ModListRenderer {

  import javaWriter._

  override def render(context: ModifiersRenderContext): Unit = {
    val annots = context.scalaMods.collect { case annot: Annot => annot }
    annotListRenderer.render(annots, onSameLine = context.annotsOnSameLine)
    renderImplicitIfNeeded(context)
    renderModifiers(context)
  }

  private def renderImplicitIfNeeded(context: ModifiersRenderContext): Unit = {
    context.scalaMods.collectFirst { case anImplicit: Implicit => anImplicit }.foreach { _ =>
      writeComment("implicit")
    }
  }

  private def renderModifiers(context: ModifiersRenderContext): Unit = {
    val sortedModifiers = context.javaModifiers.sorted(javaModifierOrdering)
    writeModifiers(sortedModifiers)
  }
}
