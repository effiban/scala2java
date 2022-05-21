package com.effiban.scala2java.stubs

import com.effiban.scala2java.{ImporteeTraverser, JavaEmitter}

import scala.meta.Importee

class StubImporteeTraverser(implicit javaEmitter: JavaEmitter) extends ImporteeTraverser {

  import javaEmitter._

  override def traverse(importee: Importee): Unit = importee match {
    case name: Importee.Name => emit(name.name.value)
    case _: Importee.Wildcard => emit("*")
    case other => throw new IllegalStateException(s"StubImporteeTraverser does not support $other")
  }
}
