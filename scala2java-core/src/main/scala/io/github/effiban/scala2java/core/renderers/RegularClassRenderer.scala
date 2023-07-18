package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.renderers.contexts.{ModifiersRenderContext, RegularClassRenderContext, TemplateRenderContext}
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Defn

trait RegularClassRenderer {
  def render(caseClass: Defn.Class, context: RegularClassRenderContext = RegularClassRenderContext()): Unit
}

private[renderers] class RegularClassRendererImpl(modListRenderer: => ModListRenderer,
                                                  typeParamListRenderer: => TypeParamListRenderer,
                                                  templateRenderer: => TemplateRenderer)
                                                 (implicit javaWriter: JavaWriter) extends RegularClassRenderer {

  import javaWriter._

  override def render(defnClass: Defn.Class, context: RegularClassRenderContext = RegularClassRenderContext()): Unit = {
    writeLine()
    val modifiersRenderContext = ModifiersRenderContext(scalaMods = defnClass.mods, javaModifiers = context.javaModifiers)
    modListRenderer.render(modifiersRenderContext)
    writeNamedType(context.javaTypeKeyword, defnClass.name.value)
    typeParamListRenderer.render(defnClass.tparams)
    val templateContext = TemplateRenderContext(
      maybeInheritanceKeyword = context.maybeInheritanceKeyword,
      permittedSubTypeNames = context.permittedSubTypeNames,
      bodyContext = context.bodyContext
    )
    templateRenderer.render(defnClass.templ, templateContext)
  }
}
