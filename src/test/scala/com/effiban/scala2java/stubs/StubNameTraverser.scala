package com.effiban.scala2java.stubs

import com.effiban.scala2java.{JavaEmitter, NameTraverser}

import scala.meta.Name

class StubNameTraverser(implicit javaEmitter: JavaEmitter) extends NameTraverser {

  import javaEmitter._

  override def traverse(name: Name): Unit = emit(name.value)
}
