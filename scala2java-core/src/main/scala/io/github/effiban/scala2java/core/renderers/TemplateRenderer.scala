package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.renderers.contexts.{InitRenderContext, TemplateRenderContext}
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.{Init, Template}

trait TemplateRenderer {

  def render(template: Template, context: TemplateRenderContext = TemplateRenderContext()): Unit
}

private[renderers] class TemplateRendererImpl(initListRenderer: => InitListRenderer,
                                              selfRenderer: SelfRenderer,
                                              templateBodyRenderer: => TemplateBodyRenderer,
                                              permittedSubTypeNameListRenderer: => PermittedSubTypeNameListRenderer)
                                             (implicit javaWriter: JavaWriter) extends TemplateRenderer {

  import javaWriter._

  def render(template: Template, context: TemplateRenderContext = TemplateRenderContext()): Unit = {
    renderTemplateInits(template.inits, context)
    selfRenderer.render(template.self)
    renderPermittedSubTypeNames(context)
    templateBodyRenderer.render(stats = template.stats, context = context.bodyContext)
  }

  private def renderTemplateInits(inits: List[Init], context: TemplateRenderContext): Unit = {
    if (inits.nonEmpty) {
      write(" ")
      context.maybeInheritanceKeyword.foreach(inheritanceKeyword => {
        writeKeyword(inheritanceKeyword)
        write(" ")
      })
      val initContext = InitRenderContext(ignoreArgs = !context.renderInitArgs, renderEmpty = context.renderInitArgs)
      initListRenderer.render(inits, initContext)
    }
  }

  private def renderPermittedSubTypeNames(context: TemplateRenderContext): Unit = {
    if (context.permittedSubTypeNames.nonEmpty) {
      write(" ")
      permittedSubTypeNameListRenderer.render(context.permittedSubTypeNames)
    }
  }
}
