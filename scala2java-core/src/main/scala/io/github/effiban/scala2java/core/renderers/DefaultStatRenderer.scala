package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.renderers.contexts.{DeclRenderContext, DefaultStatRenderContext, DefnRenderContext, EmptyStatRenderContext}
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.{Decl, Defn, Import, Pkg, Stat, Term}

trait DefaultStatRenderer {
  def render(stat: Stat, context: DefaultStatRenderContext = EmptyStatRenderContext): Unit
}

private[renderers] class DefaultStatRendererImpl(statTermRenderer: => StatTermRenderer,
                                                 importRenderer: => ImportRenderer,
                                                 declRenderer: => DeclRenderer,
                                                 defnRenderer: => DefnRenderer)
                                                (implicit javaWriter: JavaWriter) extends DefaultStatRenderer {

  import javaWriter._

  override def render(stat: Stat, context: DefaultStatRenderContext = EmptyStatRenderContext): Unit =
    (stat, context) match {
    case (term: Term, _) => statTermRenderer.render(term)
    case (`import`: Import, _) => importRenderer.render(`import`)
    case (pkg: Pkg, _) => //TODO

    case (decl: Decl, declContext: DeclRenderContext) => declRenderer.render(decl, declContext)
    case (decl: Decl, aContext) => handleInvalidContext(decl, aContext)

    case (defn: Defn, defnContext: DefnRenderContext) => defnRenderer.render(defn, defnContext)
    case (defn: Defn, aContext) => handleInvalidContext(defn, aContext)

    case (other, _) => writeComment(s"UNSUPPORTED: $other")
  }

  private def handleInvalidContext(stat: Stat, aContext: DefaultStatRenderContext): Unit = {
    throw new IllegalStateException(s"Got an invalid context $aContext for: $stat")
  }
}
