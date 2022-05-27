package com.effiban.scala2java.stubs

import com.effiban.scala2java.{BindTraverser, JavaEmitter}

import scala.meta.Pat.Bind

class StubBindTraverser(implicit javaEmitter: JavaEmitter) extends BindTraverser {
  import javaEmitter._

  override def traverse(bind: Bind): Unit = emitComment(bind.toString())
}
