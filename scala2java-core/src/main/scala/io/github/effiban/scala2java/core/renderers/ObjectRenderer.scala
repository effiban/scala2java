package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.renderers.contexts.{ModifiersRenderContext, ObjectRenderContext, TemplateRenderContext}
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Defn.Object

trait ObjectRenderer {
  def render(traitDef: Object, context: ObjectRenderContext = ObjectRenderContext()): Unit
}

private[renderers] class ObjectRendererImpl(modListRenderer: => ModListRenderer,
                                            templateRenderer: => TemplateRenderer)
                                           (implicit javaWriter: JavaWriter) extends ObjectRenderer {

  import javaWriter._

  override def render(objectDef: Object, context: ObjectRenderContext = ObjectRenderContext()): Unit = {
    writeLine()
    val modifiersRenderContext = ModifiersRenderContext(scalaMods = objectDef.mods, javaModifiers = context.javaModifiers)
    modListRenderer.render(modifiersRenderContext)
    writeNamedType(context.javaTypeKeyword, objectDef.name.value)
    val templateContext = TemplateRenderContext(
      maybeInheritanceKeyword = context.maybeInheritanceKeyword,
      bodyContext = context.bodyContext
    )
    templateRenderer.render(objectDef.templ, templateContext)
  }
}
