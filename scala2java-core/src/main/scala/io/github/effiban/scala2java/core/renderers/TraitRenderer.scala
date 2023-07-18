package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.entities.JavaKeyword
import io.github.effiban.scala2java.core.renderers.contexts.{ModifiersRenderContext, TemplateRenderContext, TraitRenderContext}
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Defn.Trait

trait TraitRenderer {
  def render(traitDef: Trait, context: TraitRenderContext = TraitRenderContext()): Unit
}

private[renderers] class TraitRendererImpl(modListRenderer: => ModListRenderer,
                                           typeParamListRenderer: => TypeParamListRenderer,
                                           templateRenderer: => TemplateRenderer)
                                          (implicit javaWriter: JavaWriter) extends TraitRenderer {

  import javaWriter._

  override def render(traitDef: Trait, context: TraitRenderContext = TraitRenderContext()): Unit = {
    writeLine()
    val modifiersRenderContext = ModifiersRenderContext(scalaMods = traitDef.mods, javaModifiers = context.javaModifiers)
    modListRenderer.render(modifiersRenderContext)
    writeNamedType(JavaKeyword.Interface, traitDef.name.value)
    typeParamListRenderer.render(traitDef.tparams)
    val templateContext = TemplateRenderContext(
      maybeInheritanceKeyword = Some(JavaKeyword.Extends),
      permittedSubTypeNames = context.permittedSubTypeNames,
      bodyContext = context.bodyContext
    )
    templateRenderer.render(traitDef.templ, templateContext)
  }
}
