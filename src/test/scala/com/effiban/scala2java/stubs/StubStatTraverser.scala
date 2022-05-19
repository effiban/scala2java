package com.effiban.scala2java.stubs

import com.effiban.scala2java.{JavaEmitter, StatTraverser}

import scala.meta.{Stat, Term}

class StubStatTraverser(implicit javaEmitter: JavaEmitter) extends StatTraverser {
  import javaEmitter._

  override def traverse(stat: Stat): Unit = stat match {
    case term: Term => emit(term.toString())
    case other => emitComment(s"StubStatTraverser doesn't support the tree $other")
  }
}
