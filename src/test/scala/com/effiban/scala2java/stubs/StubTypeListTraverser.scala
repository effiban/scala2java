package com.effiban.scala2java.stubs

import com.effiban.scala2java.{JavaEmitter, TypeListTraverser}

import scala.meta.Type

class StubTypeListTraverser(implicit javaEmitter: JavaEmitter) extends TypeListTraverser {
  import javaEmitter._

  override def traverse(types: List[Type]): Unit = emit(s"<${types.mkString(", ")}>")
}
