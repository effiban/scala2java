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
      stats.foreach(stat => {
        val statContext = context.statContextMap
          .getOrElse(stat, throw new IllegalStateException(s"No render context defined for template stat: $stat"))
        templateStatRenderer.render(stat, statContext)
      })
    }
  }
}
