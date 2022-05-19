package com.effiban.scala2java.stubs

import com.effiban.scala2java.{JavaEmitter, TermNameTraverser}

import scala.meta.Term

class StubTermNameTraverser(implicit javaEmitter: JavaEmitter) extends TermNameTraverser {
  import javaEmitter._

  override def traverse(termName: Term.Name): Unit = emit(termName.toString())
}
