package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.spi.predicates.ImporterExcludedPredicate
import io.github.effiban.scala2java.spi.transformers.ImporterTransformer

import scala.meta.{Import, Importer}

trait ImportTraverser {
  def traverse(`import`: Import): Option[Import]
}

private[traversers] class ImportTraverserImpl(importerTraverser: => ImporterTraverser,
                                              importerExcludedPredicate: ImporterExcludedPredicate,
                                              importerTransformer: ImporterTransformer) extends ImportTraverser {

  override def traverse(`import`: Import): Option[Import] = {
    val traversedImporters = `import`.importers match {
      case Nil => throw new IllegalStateException("Invalid import with no inner importers")
      case importers => importers.flatMap(flattenImportees)
        .filterNot(importerExcludedPredicate)
        .map(importerTransformer.transform)
        .distinctBy(_.structure)
        .map(importerTraverser.traverse)
    }
    Option(traversedImporters)
      .filter(_.nonEmpty)
      .map(Import(_))
  }

  private def flattenImportees(importer: Importer): List[Importer] = {
    import importer._
    importees.map(importee => Importer(ref, List(importee)))
  }
}
