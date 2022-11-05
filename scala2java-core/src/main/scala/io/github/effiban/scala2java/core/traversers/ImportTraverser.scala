package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.StatContext
import io.github.effiban.scala2java.core.entities.JavaScope
import io.github.effiban.scala2java.core.predicates.ImporterIncludedPredicate
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Import

trait ImportTraverser {
  def traverse(`import`: Import, context: StatContext = StatContext()): Unit
}

private[traversers] class ImportTraverserImpl(importerTraverser: => ImporterTraverser,
                                              importerIncludedPredicate: ImporterIncludedPredicate)
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
      case importers => importers.filter(importerIncludedPredicate).foreach(importerTraverser.traverse)
    }
  }
}
