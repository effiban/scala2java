package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.classifiers.JavaStatClassifier
import io.github.effiban.scala2java.core.renderers.contexts._
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.{Ctor, Defn, Import, Stat}

trait TemplateStatRenderer {
  def render(stat: Stat, context: TemplateStatRenderContext = EmptyStatRenderContext): Unit
}

private[renderers] class TemplateStatRendererImpl(enumConstantListRenderer: => EnumConstantListRenderer,
                                                  ctorSecondaryRenderer: => CtorSecondaryRenderer,
                                                  defaultStatRenderer: => DefaultStatRenderer,
                                                  javaStatClassifier: JavaStatClassifier)
                                                 (implicit javaWriter: JavaWriter) extends TemplateStatRenderer {

  import javaWriter._

  override def render(stat: Stat, context: TemplateStatRenderContext = EmptyStatRenderContext): Unit =
    (stat, context) match {
      case (defnVar: Defn.Var, EnumConstantListRenderContext) => renderEnumConstantList(defnVar)

      case (anImport: Import, _) => renderImportAsComment(anImport)

      case (ctorSecondary: Ctor.Secondary, ctorContext: CtorSecondaryRenderContext) =>
        ctorSecondaryRenderer.render(ctorSecondary, ctorContext)
      case (ctorSecondary: Ctor.Secondary, aContext) => handleInvalidContext(ctorSecondary, aContext)

      case (aStat, ctx) => renderDefaultStat(aStat, ctx)
    }

  private def renderEnumConstantList(defnVar: Defn.Var): Unit = {
    enumConstantListRenderer.render(defnVar)
    writeStatementEnd()
  }

  private def renderImportAsComment(anImport: Import): Unit = {
    writeComment(s"$anImport")
    writeStatementEnd()
  }

  private def renderDefaultStat(stat: Stat, context: StatRenderContext): Unit = {
    defaultStatRenderer.render(stat, context)
    if (javaStatClassifier.requiresEndDelimiter(stat)) {
      writeStatementEnd()
    }
  }

  private def handleInvalidContext(stat: Stat, aContext: TemplateStatRenderContext): Unit = {
    throw new IllegalStateException(s"Got an invalid context type $aContext for: $stat")
  }
}
