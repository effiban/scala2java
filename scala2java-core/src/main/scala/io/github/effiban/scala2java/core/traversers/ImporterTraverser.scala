package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.{DefaultTermRefRenderer, ImporteeRenderer}
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Importer

trait ImporterTraverser extends ScalaTreeTraverser[Importer]

private[traversers] class ImporterTraverserImpl(termRefTraverser: => DefaultTermRefTraverser,
                                                termRefRenderer: => DefaultTermRefRenderer,
                                                importeeRenderer: ImporteeRenderer)
                                               (implicit javaWriter: JavaWriter) extends ImporterTraverser {

  import javaWriter._

  // In Scala there can be several `import` statements on same line (not just the final name) - each one is called an 'Importer'
  override def traverse(importer: Importer): Unit = {
    importer.importees.foreach(importee => {
      write("import ")
      val traversedImporterRef = termRefTraverser.traverse(importer.ref)
      termRefRenderer.render(traversedImporterRef)
      writeQualifierSeparator()
      importeeRenderer.render(importee)
      writeStatementEnd()
    })
  }
}
