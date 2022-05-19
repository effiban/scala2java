package com.effiban.scala2java.stubs

import com.effiban.scala2java.{JavaEmitter, ReturnTraverser}

import scala.meta.Term.Return

class StubReturnTraverser(implicit javaEmitter: JavaEmitter) extends ReturnTraverser {

  import javaEmitter._

  override def traverse(`return`: Return): Unit = emit(s"return ${`return`.expr}")
}

