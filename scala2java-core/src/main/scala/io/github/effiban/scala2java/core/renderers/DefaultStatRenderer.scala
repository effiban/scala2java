package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.renderers.contexts._
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.{Decl, Defn, Import, Pkg, Stat, Term}

trait DefaultStatRenderer {
  def render(stat: Stat, context: StatRenderContext = EmptyStatRenderContext): Unit
}

private[renderers] class DefaultStatRendererImpl(statTermRenderer: => StatTermRenderer,
                                                 importRenderer: => ImportRenderer,
                                                 pkgRenderer: => PkgRenderer,
                                                 declRenderer: => DeclRenderer,
                                                 defnRenderer: => DefnRenderer)
                                                (implicit javaWriter: JavaWriter) extends DefaultStatRenderer {

  import javaWriter._

  override def render(stat: Stat, context: StatRenderContext = EmptyStatRenderContext): Unit =
    (stat, context) match {
      case (term: Term, _) => statTermRenderer.render(term)

      case (`import`: Import, _) => importRenderer.render(`import`)

      case (pkg: Pkg, pkgContext: PkgRenderContext) => pkgRenderer.render(pkg, pkgContext)
      case (pkg: Pkg, aContext) => handleInvalidContext(pkg, aContext)

      case (decl: Decl, declContext: DeclRenderContext) => declRenderer.render(decl, declContext)
      case (decl: Decl, aContext) => handleInvalidContext(decl, aContext)

      case (defn: Defn, defnContext: DefnRenderContext) => defnRenderer.render(defn, defnContext)
      case (defn: Defn, aContext) => handleInvalidContext(defn, aContext)

      case (other, _) => writeComment(s"UNSUPPORTED: $other")
    }

  private def handleInvalidContext(stat: Stat, aContext: StatRenderContext): Unit = {
    throw new IllegalStateException(s"Got an invalid context $aContext for: $stat")
  }
}
