package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.renderers.contexts.TemplateBodyRenderContext
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Stat

trait TemplateBodyRenderer {

  def render(stats: List[Stat], context: TemplateBodyRenderContext = TemplateBodyRenderContext()): Unit
}

private[renderers] class TemplateBodyRendererImpl(templateStatRenderer: => TemplateStatRenderer)
                                                 (implicit javaWriter: JavaWriter) extends TemplateBodyRenderer {

  import javaWriter._

  override def render(stats: List[Stat], context: TemplateBodyRenderContext = TemplateBodyRenderContext()): Unit = {
    writeBlockStart()
    renderContents(stats, context)
    writeBlockEnd()
  }

  private def renderContents(stats: List[Stat], context: TemplateBodyRenderContext): Unit = {
    if (stats.nonEmpty) {
      stats.foreach(stat => templateStatRenderer.render(stat, statContextOf(context, stat)))
    }
  }

  private def statContextOf(context: TemplateBodyRenderContext, stat: Stat) = {
    context.statContextMap.find { case (aStat, _) => aStat.structure == stat.structure }
      .map(_._2)
      .getOrElse(throw new IllegalStateException(s"No context defined for template body stat: $stat"))
  }
}
