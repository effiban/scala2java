package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.contexts.ModifiersRenderContext
import io.github.effiban.scala2java.core.orderings.JavaModifierOrdering
import io.github.effiban.scala2java.core.writers.JavaWriter

trait ModListRenderer {
  def render(context: ModifiersRenderContext): Unit
}

class ModListRendererImpl(annotListRenderer: => AnnotListRenderer,
                          javaModifierOrdering: JavaModifierOrdering)
                          (implicit javaWriter: JavaWriter) extends ModListRenderer {

  import javaWriter._

  override def render(context: ModifiersRenderContext): Unit = {
    annotListRenderer.render(context.annots, onSameLine = context.annotsOnSameLine)
    renderImplicitIfNeeded(context)
    renderModifiers(context)
  }

  private def renderImplicitIfNeeded(context: ModifiersRenderContext): Unit = {
    if (context.hasImplicit) {
      writeComment("implicit")
    }
  }

  private def renderModifiers(context: ModifiersRenderContext): Unit = {
    val sortedModifiers = context.javaModifiers.sorted(javaModifierOrdering)
    writeModifiers(sortedModifiers)
  }
}
