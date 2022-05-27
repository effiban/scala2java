package com.effiban.scala2java.stubs

import com.effiban.scala2java.{JavaEmitter, PatSeqWildcardTraverser}

import scala.meta.Pat

class StubPatSeqWildcardTraverser(implicit javaEmitter: JavaEmitter) extends PatSeqWildcardTraverser {
  import javaEmitter._

  override def traverse(patSeqWildcard: Pat.SeqWildcard): Unit = emitComment("...")
}
