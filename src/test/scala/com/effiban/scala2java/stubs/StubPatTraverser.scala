package com.effiban.scala2java.stubs

import com.effiban.scala2java.{JavaEmitter, PatTraverser}

import scala.meta.{Lit, Pat}

class StubPatTraverser(implicit javaEmitter: JavaEmitter) extends PatTraverser {
  import javaEmitter._

  override def traverse(pat: Pat): Unit = pat match {
    case litStr: Lit.String => emit(s"\"${litStr.value}\"")
    case other => emit(other.toString())
  }
}
