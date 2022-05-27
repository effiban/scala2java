package com.effiban.scala2java.stubs

import com.effiban.scala2java.{JavaEmitter, PatTupleTraverser}

import scala.meta.Pat

class StubPatTupleTraverser(implicit javaEmitter: JavaEmitter) extends PatTupleTraverser {
  import javaEmitter._

  override def traverse(patTuple: Pat.Tuple): Unit = emitComment(s"(${patTuple.args.mkString(", ")})")
}
