package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.renderers.contexts.{DefaultStatRenderContext, EmptyStatRenderContext}
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.{Decl, Defn, Import, Pkg, Stat, Term}

trait DefaultStatRenderer {
  def render(stat: Stat, context: DefaultStatRenderContext = EmptyStatRenderContext): Unit
}

private[renderers] class DefaultStatRendererImpl(statTermRenderer: => StatTermRenderer,
                                                 importRenderer: => ImportRenderer)
                                                (implicit javaWriter: JavaWriter) extends DefaultStatRenderer {

  import javaWriter._

  override def render(stat: Stat, context: DefaultStatRenderContext = EmptyStatRenderContext): Unit = stat match {
    case term: Term => statTermRenderer.render(term)
    case `import`: Import => importRenderer.render(`import`)
    case pkg: Pkg => //TODO
    case defn: Defn => //TODO
    case decl: Decl => //TODO
    case other => writeComment(s"UNSUPPORTED: $other")
  }
}
