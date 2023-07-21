package io.github.effiban.scala2java.core.renderers


import io.github.effiban.scala2java.core.renderers.contexts.PkgRenderContext
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.{Pkg, Stat}

trait PkgRenderer {
  def render(pkg: Pkg, context: PkgRenderContext = PkgRenderContext()): Unit
}

private[renderers] class PkgRendererImpl(defaultTermRefRenderer: => DefaultTermRefRenderer,
                                         defaultStatRenderer: => DefaultStatRenderer)
                                        (implicit javaWriter: JavaWriter) extends PkgRenderer {

  import javaWriter._

  override def render(pkg: Pkg, context: PkgRenderContext = PkgRenderContext()): Unit = {
    write("package ")
    defaultTermRefRenderer.render(pkg.ref)
    writeStatementEnd()
    writeLine()

    pkg.stats.foreach(stat => defaultStatRenderer.render(stat, statContextOf(context, stat)))
  }

  private def statContextOf(context: PkgRenderContext, stat: Stat) = {
    context.statContextMap.find { case (aStat, _) => aStat.structure == stat.structure }
      .map(_._2)
      .getOrElse(throw new IllegalStateException(s"No context defined for package stat: $stat"))
  }
}
