package effiban.scala2java.traversers

import effiban.scala2java.JavaEmitter

import scala.meta.Import

trait ImportTraverser extends ScalaTreeTraverser[Import]

private[scala2java] class ImportTraverserImpl(importerTraverser: => ImporterTraverser)
                                             (implicit javaEmitter: JavaEmitter) extends ImportTraverser {

  import javaEmitter._

  override def traverse(`import`: Import): Unit = {
    `import`.importers match {
      case List() => emitComment("Invalid import with no inner importers")
      case importers => importers.foreach(importerTraverser.traverse)
    }
  }
}

object ImportTraverser extends ImportTraverserImpl(ImporterTraverser)
