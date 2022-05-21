package com.effiban.scala2java.stubs

import com.effiban.scala2java.{JavaEmitter, TermRefTraverser}

import scala.meta.Term

class StubTermRefTraverser(implicit javaEmitter: JavaEmitter) extends TermRefTraverser {

  import javaEmitter._

  override def traverse(termRef: Term.Ref): Unit = emit(termRef.toString())
}
