package com.effiban.scala2java.stubs

import com.effiban.scala2java.{JavaEmitter, TermFunctionTraverser}

import scala.meta.Term

class StubTermFunctionTraverser(implicit javaEmitter: JavaEmitter) extends TermFunctionTraverser {
  import javaEmitter._

  override def traverse(termFunction: Term.Function): Unit = emit(termFunction.toString())
}
