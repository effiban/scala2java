package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.entities.JavaKeyword
import io.github.effiban.scala2java.core.renderers.contexts.{CaseClassRenderContext, ModifiersRenderContext, TemplateRenderContext}
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Defn

trait CaseClassRenderer {
  def render(caseClass: Defn.Class, context: CaseClassRenderContext = CaseClassRenderContext()): Unit
}

private[renderers] class CaseClassRendererImpl(modListRenderer: => ModListRenderer,
                                               typeParamListRenderer: => TypeParamListRenderer,
                                               termParamListRenderer: => TermParamListRenderer,
                                               templateRenderer: => TemplateRenderer)
                                              (implicit javaWriter: JavaWriter) extends CaseClassRenderer {

  import javaWriter._

  override def render(caseClass: Defn.Class, context: CaseClassRenderContext = CaseClassRenderContext()): Unit = {
    writeLine()
    val modifiersRenderContext = ModifiersRenderContext(scalaMods = caseClass.mods, javaModifiers = context.javaModifiers)
    modListRenderer.render(modifiersRenderContext)
    writeNamedType(JavaKeyword.Record, caseClass.name.value)
    typeParamListRenderer.render(caseClass.tparams)
    termParamListRenderer.render(caseClass.ctor.paramss.flatten)
    val templateContext = TemplateRenderContext(
      maybeInheritanceKeyword = context.maybeInheritanceKeyword,
      bodyContext = context.bodyContext
    )
    templateRenderer.render(caseClass.templ, templateContext)
  }
}
