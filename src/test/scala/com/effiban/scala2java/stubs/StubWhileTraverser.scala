package com.effiban.scala2java.stubs

import com.effiban.scala2java.{JavaEmitter, WhileTraverser}

import scala.meta.Term.While

class StubWhileTraverser(implicit javaEmitter: JavaEmitter) extends WhileTraverser {

  import javaEmitter._

  override def traverse(`while`: While): Unit = {
    emit(s"while (${`while`.expr})")
    emitBlockStart()
    emitLine(`while`.body.toString())
    emitBlockEnd()
  }
}
