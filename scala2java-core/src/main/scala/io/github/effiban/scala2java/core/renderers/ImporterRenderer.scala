package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Importer

trait ImporterRenderer extends JavaTreeRenderer[Importer]

private[renderers] class ImporterRendererImpl(termRefRenderer: => DefaultTermRefRenderer,
                                              importeeRenderer: ImporteeRenderer)
                                             (implicit javaWriter: JavaWriter) extends ImporterRenderer {

  import javaWriter._

  // In Scala there can be several `import` statements on same line (not just the final name) - each one is called an 'Importer'
  override def render(importer: Importer): Unit = {
    importer.importees.foreach(importee => {
      write("import ")
      termRefRenderer.render(importer.ref)
      writeQualifierSeparator()
      importeeRenderer.render(importee)
      writeStatementEnd()
    })
  }
}
