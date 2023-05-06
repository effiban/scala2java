package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.contexts.StatContext
import io.github.effiban.scala2java.core.writers.JavaWriter
import io.github.effiban.scala2java.spi.entities.JavaScope

import scala.meta.Import

trait ImportRenderer {
  def render(`import`: Import, context: StatContext = StatContext()): Unit
}

private[renderers] class ImportRendererImpl(importerRenderer: => ImporterRenderer)
                                           (implicit javaWriter: JavaWriter) extends ImportRenderer {

  import javaWriter._

  override def render(`import`: Import, context: StatContext = StatContext()): Unit = {
    context.javaScope match {
      case JavaScope.Package => traverseInner(`import`)
      case _ =>
        // Java doesn't support imports below package scope
        writeComment(s"${`import`.toString()}")
    }
  }

  private def traverseInner(`import`: Import): Unit = {
    `import`.importers.foreach(importerRenderer.render)
  }
}
