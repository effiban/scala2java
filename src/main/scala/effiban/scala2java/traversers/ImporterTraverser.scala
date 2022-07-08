package effiban.scala2java.traversers

import effiban.scala2java.JavaEmitter

import scala.meta.Term.Select
import scala.meta.{Importer, Term}

trait ImporterTraverser extends ScalaTreeTraverser[Importer]

private[scala2java] class ImporterTraverserImpl(termRefTraverser: => TermRefTraverser,
                                                importeeTraverser: => ImporteeTraverser)
                                               (implicit javaEmitter: JavaEmitter) extends ImporterTraverser {

  import javaEmitter._

  // In Scala there can be several `import` statements on same line (not just the final name) - each one is called an 'Importer'
  override def traverse(importer: Importer): Unit = {
    importer.ref match {
      case Term.Name("scala") =>
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
