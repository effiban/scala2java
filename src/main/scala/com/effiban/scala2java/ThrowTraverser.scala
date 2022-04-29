package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.{emit, emitStatementEnd}

import scala.meta.Term.Throw

trait ThrowTraverser extends ScalaTreeTraverser[Throw]

object ThrowTraverser extends ThrowTraverser {

  override def traverse(`throw`: Throw): Unit = {
    emit("throw ")
    TermTraverser.traverse(`throw`.expr)
    emitStatementEnd()
  }
}
