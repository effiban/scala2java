package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.StatContext
import io.github.effiban.scala2java.core.renderers.ImportRenderer
import io.github.effiban.scala2java.spi.predicates.ImporterExcludedPredicate
import io.github.effiban.scala2java.spi.transformers.ImporterTransformer

import scala.meta.{Import, Importer}

trait ImportTraverser {
  def traverse(`import`: Import, context: StatContext = StatContext()): Unit
}

private[traversers] class ImportTraverserImpl(importerTraverser: => ImporterTraverser,
                                              importerExcludedPredicate: ImporterExcludedPredicate,
                                              importerTransformer: ImporterTransformer,
                                              importRenderer: => ImportRenderer) extends ImportTraverser {

  override def traverse(`import`: Import, context: StatContext = StatContext()): Unit = {
    val traversedImporters = `import`.importers match {
      case Nil => throw new IllegalStateException("Invalid import with no inner importers")
      case importers => importers.flatMap(flattenImportees)
        .filterNot(importerExcludedPredicate)
        .map(importerTransformer.transform)
        .distinctBy(_.structure)
        .map(importerTraverser.traverse)
    }
    if (traversedImporters.nonEmpty) {
      importRenderer.render(Import(traversedImporters), context)
    }
  }

  private def flattenImportees(importer: Importer): List[Importer] = {
    import importer._
    importees.map(importee => Importer(ref, List(importee)))
  }
}
