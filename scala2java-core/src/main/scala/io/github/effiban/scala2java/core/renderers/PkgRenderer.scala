package io.github.effiban.scala2java.core.renderers


import io.github.effiban.scala2java.core.entities.TreeKeyedMaps
import io.github.effiban.scala2java.core.renderers.contexts.PkgRenderContext
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Pkg

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

    pkg.stats.foreach(stat =>
      defaultStatRenderer.render(stat, TreeKeyedMaps.get(context.statContextMap, stat))
    )
  }
}
