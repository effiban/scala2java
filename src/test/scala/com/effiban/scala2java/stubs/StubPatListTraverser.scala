package com.effiban.scala2java.stubs

import com.effiban.scala2java.{JavaEmitter, PatListTraverser}

import scala.meta.Pat

class StubPatListTraverser(implicit javaEmitter: JavaEmitter) extends PatListTraverser {
  import javaEmitter._

  override def traverse(pats: List[Pat]): Unit = {
    val patsStr = pats.map(_.toString()).mkString(", ")
    emit(patsStr)
  }
}
