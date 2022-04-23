package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.{emit, emitStatementEnd}

import scala.meta.Stat

object LastStatementTraverser {

  def traverseLastStatement(stmt: Stat): Unit = {
    emit("return ")
    StatTraverser.traverse(stmt)
    emitStatementEnd()
  }
}
