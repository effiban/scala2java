package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.ImporterRenderer

import scala.meta.Importer

trait ImporterTraverser extends ScalaTreeTraverser[Importer]

private[traversers] class ImporterTraverserImpl(defaultTermRefTraverser: => DefaultTermRefTraverser,
                                                importerRenderer: => ImporterRenderer) extends ImporterTraverser {

  // In Scala there can be several `import` statements on same line (not just the final name) - each one is called an 'Importer'
  override def traverse(importer: Importer): Unit = {
    val traversedImporter = importer.copy(ref = defaultTermRefTraverser.traverse(importer.ref))
    importerRenderer.render(traversedImporter)
  }
}
