package com.effiban.scala2java

import scala.meta.Importee

trait ImporteeTraverser extends ScalaTreeTraverser[Importee]

private[scala2java] class ImporteeTraverserImpl(nameTraverser: => NameTraverser)
                                               (implicit javaEmitter: JavaEmitter) extends ImporteeTraverser {
  import javaEmitter._

  // A single imported element within an Importer (can be one name, wildcard etc. see below)
  override def traverse(importee: Importee): Unit = {
    importee match {
      case Importee.Name(name) => nameTraverser.traverse(name)
      case Importee.Wildcard() => emit("*")
      case Importee.Rename(name, rename) =>
        nameTraverser.traverse(name)
        emit(" ")
        emitComment(s"Renamed in Scala to '${rename.toString}'")
      case Importee.Unimport(name) =>
        nameTraverser.traverse(name)
        emit(" ")
        emitComment(s"Hidden (unimported) in Scala")
    }
  }
}

object ImporteeTraverser extends ImporteeTraverserImpl(NameTraverser)
