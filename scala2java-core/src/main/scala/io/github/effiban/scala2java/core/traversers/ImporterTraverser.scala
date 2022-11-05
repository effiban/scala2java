package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Importer

trait ImporterTraverser extends ScalaTreeTraverser[Importer]

private[traversers] class ImporterTraverserImpl(termRefTraverser: => TermRefTraverser,
                                                importeeTraverser: => ImporteeTraverser)
                                               (implicit javaWriter: JavaWriter) extends ImporterTraverser {

  import javaWriter._

  // In Scala there can be several `import` statements on same line (not just the final name) - each one is called an 'Importer'
  override def traverse(importer: Importer): Unit = {
    importer.importees.foreach(importee => {
      write("import ")
      termRefTraverser.traverse(importer.ref)
      writeQualifierSeparator()
      importeeTraverser.traverse(importee)
      writeStatementEnd()
    })
  }
}
