package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.renderers.contexts.ImportRenderContext
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Import

trait ImportRenderer {
  def render(`import`: Import, context: ImportRenderContext = ImportRenderContext()): Unit
}

private[renderers] class ImportRendererImpl(importerRenderer: => ImporterRenderer)
                                           (implicit javaWriter: JavaWriter) extends ImportRenderer {

  import javaWriter._

  override def render(`import`: Import, context: ImportRenderContext = ImportRenderContext()): Unit = {
    if (context.asComment) writeComment(s"${`import`.toString()}") else renderInner(`import`)
  }

  private def renderInner(`import`: Import): Unit = {
    `import`.importers.foreach(importerRenderer.render)
  }
}
