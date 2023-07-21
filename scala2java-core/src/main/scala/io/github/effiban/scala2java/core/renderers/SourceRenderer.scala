package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.entities.TreeKeyedMaps
import io.github.effiban.scala2java.core.renderers.contexts.SourceRenderContext

import scala.meta.Source

trait SourceRenderer {
  def render(source: Source, context: SourceRenderContext): Unit
}

private[renderers] class SourceRendererImpl(defaultStatRenderer: => DefaultStatRenderer) extends SourceRenderer {

  def render(source: Source, context: SourceRenderContext): Unit = {
    source.stats.foreach(stat =>
      defaultStatRenderer.render(stat, TreeKeyedMaps.get(context.statContextMap, stat))
    )
  }
}
