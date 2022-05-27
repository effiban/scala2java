package com.effiban.scala2java.stubs

import com.effiban.scala2java.{JavaEmitter, PatVarTraverser}

import scala.meta.Pat

class StubPatVarTraverser(implicit javaEmitter: JavaEmitter) extends PatVarTraverser {
  import javaEmitter._

  override def traverse(patVar: Pat.Var): Unit = emit(patVar.name.value)
}
