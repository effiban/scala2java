package com.effiban.scala2java.stubs

import com.effiban.scala2java.{JavaEmitter, TypeNameTraverser}

import scala.meta.Type

class StubTypeNameTraverser(implicit javaEmitter: JavaEmitter) extends TypeNameTraverser {
  import javaEmitter._

  override def traverse(typeName: Type.Name): Unit = emit(typeName.toString())
}
