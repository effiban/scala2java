package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emitComment

import scala.meta.Import

object ImportTraverser extends ScalaTreeTraverser[Import] {

  override def traverse(`import`: Import): Unit = {
    `import`.importers match {
      case List() => emitComment("Invalid import with no inner importers")
      case importers => importers.foreach(GenericTreeTraverser.traverse)
    }
  }
}
