package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.{emit, emitStatementEnd}

import scala.meta.Term.Throw

object ThrowTraverser extends ScalaTreeTraverser[Throw] {

  override def traverse(`throw`: Throw): Unit = {
    emit("throw ")
    GenericTreeTraverser.traverse(`throw`.expr)
    emitStatementEnd()
  }
}
