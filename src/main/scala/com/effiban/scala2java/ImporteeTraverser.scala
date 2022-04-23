package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.{emit, emitComment}

import scala.meta.Importee

object ImporteeTraverser extends ScalaTreeTraverser[Importee] {

  // A single imported element within an Importer (can be one name, wildcard etc. see below)
  override def traverse(importee: Importee): Unit = {
    importee match {
      case Importee.Name(name) => NameTraverser.traverse(name)
      case Importee.Wildcard() => emit("*")
      case Importee.Rename(name, rename) =>
        NameTraverser.traverse(name)
        emitComment(s" Renamed in Scala to ${rename.toString}")
      case Importee.Unimport(name) =>
        NameTraverser.traverse(name)
        emitComment(s" Hidden (unimported) in Scala")
    }
  }
}
