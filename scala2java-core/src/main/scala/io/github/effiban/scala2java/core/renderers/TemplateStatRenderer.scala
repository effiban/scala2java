package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.classifiers.JavaStatClassifier
import io.github.effiban.scala2java.core.renderers.contexts.{DefaultStatRenderContext, TemplateStatRenderContext}
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.{Defn, Stat}

trait TemplateStatRenderer {
  def render(stat: Stat, context: TemplateStatRenderContext = TemplateStatRenderContext()): Unit
}

private[renderers] class TemplateStatRendererImpl(enumConstantListRenderer: => EnumConstantListRenderer,
                                                  defaultStatRenderer: => DefaultStatRenderer,
                                                  javaStatClassifier: JavaStatClassifier)
                                                 (implicit javaWriter: JavaWriter) extends TemplateStatRenderer {

  import javaWriter._

  override def render(stat: Stat, context: TemplateStatRenderContext = TemplateStatRenderContext()): Unit =
    stat match {
      case defnVar: Defn.Var if context.enumConstantList => renderEnumConstantList(defnVar)
      case aStat => renderDefaultStat(aStat)
    }

  private def renderEnumConstantList(defnVar: Defn.Var): Unit = {
    enumConstantListRenderer.render(defnVar)
    writeStatementEnd()
  }

  private def renderDefaultStat(stat: Stat): Unit = {
    defaultStatRenderer.render(stat, DefaultStatRenderContext(importAsComment = true))
    if (javaStatClassifier.requiresEndDelimiter(stat)) {
      writeStatementEnd()
    }
  }
}
