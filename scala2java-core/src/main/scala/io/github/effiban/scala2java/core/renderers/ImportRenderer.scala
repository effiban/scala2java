package io.github.effiban.scala2java.core.renderers

import scala.meta.Import

trait ImportRenderer extends JavaTreeRenderer[Import]

private[renderers] class ImportRendererImpl(importerRenderer: => ImporterRenderer) extends ImportRenderer {

  override def render(`import`: Import): Unit = {
    `import`.importers.foreach(importerRenderer.render)
  }
}
