package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.classifiers.JavaStatClassifier
import io.github.effiban.scala2java.core.renderers.contexts.{EmptyStatRenderContext, EnumConstantListRenderContext, StatRenderContext, TemplateStatRenderContext}
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.{Defn, Import, Stat}

trait TemplateStatRenderer {
  def render(stat: Stat, context: TemplateStatRenderContext = EmptyStatRenderContext): Unit
}

private[renderers] class TemplateStatRendererImpl(enumConstantListRenderer: => EnumConstantListRenderer,
                                                  defaultStatRenderer: => DefaultStatRenderer,
                                                  javaStatClassifier: JavaStatClassifier)
                                                 (implicit javaWriter: JavaWriter) extends TemplateStatRenderer {

  import javaWriter._

  override def render(stat: Stat, context: TemplateStatRenderContext = EmptyStatRenderContext): Unit =
    (stat, context) match {
      case (defnVar: Defn.Var, EnumConstantListRenderContext) => renderEnumConstantList(defnVar)
      case (anImport: Import, _) => writeComment(s"$anImport")
      case (aStat, ctx) => renderDefaultStat(aStat, ctx)
    }

  private def renderEnumConstantList(defnVar: Defn.Var): Unit = {
    enumConstantListRenderer.render(defnVar)
    writeStatementEnd()
  }

  private def renderDefaultStat(stat: Stat, context: StatRenderContext): Unit = {
    defaultStatRenderer.render(stat, context)
    if (javaStatClassifier.requiresEndDelimiter(stat)) {
      writeStatementEnd()
    }
  }
}
