package com.effiban.scala2java.stubs

import com.effiban.scala2java.{BlockTraverser, JavaEmitter}

import scala.meta.{Init, Term}

class StubBlockTraverser(implicit javaEmitter: JavaEmitter) extends BlockTraverser{
  import javaEmitter._

  override def traverse(block: Term.Block, shouldReturnValue: Boolean, maybeInit: Option[Init] = None): Unit = {
    emitLine()
    emitComment(
      s"""STUB BLOCK
         |Input Init: ${maybeInit.getOrElse("None")}
         |Input shouldReturnValue: $shouldReturnValue
         |Scala Body:
         |$block""".stripMargin
    )
  }
}
