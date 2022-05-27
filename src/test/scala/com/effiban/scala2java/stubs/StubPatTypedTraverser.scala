package com.effiban.scala2java.stubs

import com.effiban.scala2java.{JavaEmitter, PatTypedTraverser}

import scala.meta.Pat

class StubPatTypedTraverser(implicit javaEmitter: JavaEmitter) extends PatTypedTraverser {
  import javaEmitter._

  override def traverse(patTyped: Pat.Typed): Unit = emit(s"${patTyped.rhs} ${patTyped.lhs}")
}
