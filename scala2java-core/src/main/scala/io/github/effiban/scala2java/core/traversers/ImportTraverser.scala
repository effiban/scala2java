package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.StatContext
import io.github.effiban.scala2java.core.writers.JavaWriter
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.spi.predicates.ImporterExcludedPredicate
import io.github.effiban.scala2java.spi.transformers.ImporterTransformer

import scala.meta.{Import, Importer}

trait ImportTraverser {
  def traverse(`import`: Import, context: StatContext = StatContext()): Unit
}

private[traversers] class ImportTraverserImpl(importerTraverser: => ImporterTraverser,
                                              importerExcludedPredicate: ImporterExcludedPredicate,
                                              importerTransformer: ImporterTransformer)
                                             (implicit javaWriter: JavaWriter) extends ImportTraverser {

  import javaWriter._

  override def traverse(`import`: Import, context: StatContext = StatContext()): Unit = {
    context.javaScope match {
      case JavaScope.Package => traverseInner(`import`)
      case _ =>
        // Java doesn't support imports below package scope
        writeComment(s"${`import`.toString()}")
    }
  }

  private def traverseInner(`import`: Import): Unit = {
    `import`.importers match {
      case Nil => throw new IllegalStateException("Invalid import with no inner importers")
      case importers => importers.flatMap(flattenImportees)
        .filterNot(importerExcludedPredicate)
        .map(importerTransformer.transform)
        .foreach(importerTraverser.traverse)
    }
  }

  private def flattenImportees(importer: Importer): List[Importer] = {
    import importer._
    importees.map(importee => Importer(ref, List(importee)))
  }
}
