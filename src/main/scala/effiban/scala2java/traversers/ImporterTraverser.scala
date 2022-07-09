package effiban.scala2java.traversers

import effiban.scala2java.writers.JavaWriter

import scala.meta.Term.Select
import scala.meta.{Importer, Term}

trait ImporterTraverser extends ScalaTreeTraverser[Importer]

private[traversers] class ImporterTraverserImpl(termRefTraverser: => TermRefTraverser,
                                                importeeTraverser: => ImporteeTraverser)
                                               (implicit javaWriter: JavaWriter) extends ImporterTraverser {

  import javaWriter._

  // In Scala there can be several `import` statements on same line (not just the final name) - each one is called an 'Importer'
  override def traverse(importer: Importer): Unit = {
    importer.ref match {
      case Term.Name("scala") =>
      case Select(Term.Name("scala"), _) =>
      case ref =>
        importer.importees.foreach(importee => {
          write("import ")
          termRefTraverser.traverse(ref)
          write(".")
          importeeTraverser.traverse(importee)
          writeStatementEnd()
        })
    }
  }
}
