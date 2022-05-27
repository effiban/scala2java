package com.effiban.scala2java.stubs

import com.effiban.scala2java.{JavaEmitter, PatWildcardTraverser}

import scala.meta.Pat

class StubPatWildcardTraverser(implicit javaEmitter: JavaEmitter) extends PatWildcardTraverser {
  import javaEmitter._

  override def traverse(patWildcard: Pat.Wildcard): Unit = emit("default")
}
