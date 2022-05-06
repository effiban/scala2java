package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.{emit, emitStatementEnd}

import scala.meta.Term.Throw

trait ThrowTraverser extends ScalaTreeTraverser[Throw]

private[scala2java] class ThrowTraverserImpl(termTraverser: => TermTraverser)
                                            (implicit javaEmitter: JavaEmitter) extends ThrowTraverser {

  override def traverse(`throw`: Throw): Unit = {
    emit("throw ")
    termTraverser.traverse(`throw`.expr)
    emitStatementEnd()
  }
}

object ThrowTraverser extends ThrowTraverserImpl(TermTraverser)
