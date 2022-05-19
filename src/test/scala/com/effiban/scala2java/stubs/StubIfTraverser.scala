package com.effiban.scala2java.stubs

import com.effiban.scala2java.{IfTraverser, JavaEmitter}

import scala.meta.Lit
import scala.meta.Term.If

class StubIfTraverser(implicit javaEmitter: JavaEmitter) extends IfTraverser {

  import javaEmitter._

  override def traverse(`if`: If, shouldReturnValue: Boolean = false): Unit = {
    emit(s"if (${`if`.cond})")
    emitBlockStart()
    emit(`if`.thenp.toString())
    emitComment(s"shouldReturnValue=$shouldReturnValue")
    emitLine()
    emitBlockEnd()
    `if`.elsep match {
      case Lit.Unit() =>
      case elsep =>
        emit("else")
        emitBlockStart()
        emit(elsep.toString())
        emitBlockEnd()
    }

  }
}
