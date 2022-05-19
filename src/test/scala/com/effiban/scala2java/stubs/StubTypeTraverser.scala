package com.effiban.scala2java.stubs

import com.effiban.scala2java.{JavaEmitter, TypeTraverser}

import scala.meta.Type

class StubTypeTraverser(implicit javaEmitter: JavaEmitter) extends TypeTraverser {
  import javaEmitter._

  override def traverse(tpe: Type): Unit = {
    tpe match {
      case name: Type.Name => emit(name.value)
      case _: Type.AnonymousName =>
      case other => throw new IllegalStateException(s"StubTypeTraverser does not support the type $other")
    }
  }
}
