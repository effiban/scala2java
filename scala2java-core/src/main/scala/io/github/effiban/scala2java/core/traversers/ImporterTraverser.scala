package io.github.effiban.scala2java.core.traversers

import scala.meta.Importer

trait ImporterTraverser extends ScalaTreeTraverser1[Importer]

private[traversers] class ImporterTraverserImpl(defaultTermRefTraverser: => DefaultTermRefTraverser) extends ImporterTraverser {

  // In Scala there can be several `import` statements on same line (not just the final name) - each one is called an 'Importer'
  override def traverse(importer: Importer): Importer = {
    importer.copy(ref = defaultTermRefTraverser.traverse(importer.ref))
  }
}
