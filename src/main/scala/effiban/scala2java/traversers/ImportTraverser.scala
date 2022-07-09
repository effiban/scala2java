package effiban.scala2java.traversers

import effiban.scala2java.writers.JavaWriter

import scala.meta.Import

trait ImportTraverser extends ScalaTreeTraverser[Import]

private[traversers] class ImportTraverserImpl(importerTraverser: => ImporterTraverser)
                                             (implicit javaWriter: JavaWriter) extends ImportTraverser {

  import javaWriter._

  override def traverse(`import`: Import): Unit = {
    `import`.importers match {
      case List() => writeComment("Invalid import with no inner importers")
      case importers => importers.foreach(importerTraverser.traverse)
    }
  }
}
