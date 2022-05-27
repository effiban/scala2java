package com.effiban.scala2java.stubs

import com.effiban.scala2java.{JavaEmitter, PatExtractInfixTraverser}

import scala.meta.Pat

class StubPatExtractInfixTraverser(implicit javaEmitter: JavaEmitter) extends PatExtractInfixTraverser {
  import javaEmitter._

  override def traverse(patExtractInfix: Pat.ExtractInfix): Unit =
    emitComment(s"${patExtractInfix.op}(${(patExtractInfix.lhs :: patExtractInfix.rhs).mkString(", ")})")
}
