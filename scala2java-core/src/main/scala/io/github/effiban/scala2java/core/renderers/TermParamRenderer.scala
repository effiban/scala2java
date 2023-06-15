package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.contexts.{ModifiersRenderContext, TermParamRenderContext}
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.{Mod, Term}

trait TermParamRenderer {
  def render(termParam: Term.Param, context: TermParamRenderContext = TermParamRenderContext()): Unit
}

private[renderers] class TermParamRendererImpl(modListRenderer: => ModListRenderer,
                                               typeRenderer: => TypeRenderer,
                                               nameRenderer: NameRenderer)
                                              (implicit javaWriter: JavaWriter) extends TermParamRenderer {

  import javaWriter._

  // method/lambda parameter declaration
  // Note that a primary ctor. param in Scala is also a class member which requires additional handling,
  // but that aspect will be handled by one of the parent renderers before this one is called
  override def render(termParam: Term.Param, context: TermParamRenderContext = TermParamRenderContext()): Unit = {
    val modifiersRenderContext = generateModifiersRenderContext(termParam.mods, context)
    modListRenderer.render(modifiersRenderContext)
    termParam.decltpe.foreach(declType => {
      typeRenderer.render(declType)
      write(" ")
    })
    nameRenderer.render(termParam.name)
    termParam.default.foreach(default => writeComment(s"= ${default.toString()}"))
  }

  private def generateModifiersRenderContext(scalaMods: List[Mod], context: TermParamRenderContext) = {
    ModifiersRenderContext(
      scalaMods = scalaMods,
      annotsOnSameLine = true,
      javaModifiers = context.javaModifiers
    )
  }

}
