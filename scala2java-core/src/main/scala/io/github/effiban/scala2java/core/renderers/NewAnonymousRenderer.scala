package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.entities.JavaKeyword
import io.github.effiban.scala2java.core.renderers.contexts.{EmptyStatRenderContext, TemplateBodyRenderContext, TemplateRenderContext, TemplateStatRenderContext}
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Term.NewAnonymous
import scala.meta.{Stat, Template}

trait NewAnonymousRenderer extends JavaTreeRenderer[NewAnonymous]

private[renderers] class NewAnonymousRendererImpl(templateRenderer: => TemplateRenderer)
                                                 (implicit javaWriter: JavaWriter) extends NewAnonymousRenderer {

  import javaWriter._

  override def render(newAnonymous: NewAnonymous): Unit = {
    writeKeyword(JavaKeyword.New)
    // TODO - fully support the body by using the correct stat contexts, passed as input
    val templateContext = templateContextWithEmptyStatContextsFor(newAnonymous.templ)
    templateRenderer.render(newAnonymous.templ, templateContext)
  }

  private def templateContextWithEmptyStatContextsFor(template: Template) = {
    val statContextMap: Map[Stat, TemplateStatRenderContext] = template.stats
      .map(stat => (stat, EmptyStatRenderContext))
      .toMap
    TemplateRenderContext(
      renderInitArgs = true,
      bodyContext = TemplateBodyRenderContext(statContextMap)
    )
  }
}
