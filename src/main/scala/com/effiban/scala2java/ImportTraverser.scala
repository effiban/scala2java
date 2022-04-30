package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emitComment

import scala.meta.Import

trait ImportTraverser extends ScalaTreeTraverser[Import]

private[scala2java] class ImportTraverserImpl(importerTraverser: => ImporterTraverser) extends ImportTraverser {

  override def traverse(`import`: Import): Unit = {
    `import`.importers match {
      case List() => emitComment("Invalid import with no inner importers")
      case importers => importers.foreach(importerTraverser.traverse)
    }
  }
}

object ImportTraverser extends ImportTraverserImpl(ImporterTraverser)
