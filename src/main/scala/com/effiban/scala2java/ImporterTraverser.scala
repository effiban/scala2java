package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.{emit, emitStatementEnd}

import scala.meta.Term.Select
import scala.meta.{Importer, Term}

trait ImporterTraverser extends ScalaTreeTraverser[Importer]

private[scala2java] class ImporterTraverserImpl(termRefTraverser: => TermRefTraverser,
                                                importeeTraverser: => ImporteeTraverser) extends ImporterTraverser {

  // In Scala there can be several `import` statements on same line (not just the final name) - each one is called an 'Importer'
  override def traverse(importer: Importer): Unit = {
    importer.ref match {
      case Select(Term.Name("scala"), _) =>
      case ref =>
        importer.importees.foreach(importee => {
          emit("import ")
          termRefTraverser.traverse(ref)
          emit(".")
          importeeTraverser.traverse(importee)
          emitStatementEnd()
        })
    }
  }
}

object ImporterTraverser extends ImporterTraverserImpl(TermRefTraverser, ImporteeTraverser)
