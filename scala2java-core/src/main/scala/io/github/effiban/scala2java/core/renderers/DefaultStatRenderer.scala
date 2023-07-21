package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.renderers.contexts.{DeclRenderContext, DefaultStatRenderContext, EmptyStatRenderContext}
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.{Decl, Defn, Import, Pkg, Stat, Term}

trait DefaultStatRenderer {
  def render(stat: Stat, context: DefaultStatRenderContext = EmptyStatRenderContext): Unit
}

private[renderers] class DefaultStatRendererImpl(statTermRenderer: => StatTermRenderer,
                                                 importRenderer: => ImportRenderer,
                                                 declRenderer: => DeclRenderer)
                                                (implicit javaWriter: JavaWriter) extends DefaultStatRenderer {

  import javaWriter._

  override def render(stat: Stat, context: DefaultStatRenderContext = EmptyStatRenderContext): Unit =
    (stat, context) match {
    case (term: Term, _) => statTermRenderer.render(term)
    case (`import`: Import, _) => importRenderer.render(`import`)
    case (pkg: Pkg, _) => //TODO
    case (defn: Defn, _) => //TODO
    case (decl: Decl, declContext: DeclRenderContext) => declRenderer.render(decl, declContext)
    case (decl: Decl, aContext) => throw new IllegalStateException(s"Mismatching context $aContext for $decl")
    case (other, _) => writeComment(s"UNSUPPORTED: $other")
  }
}
