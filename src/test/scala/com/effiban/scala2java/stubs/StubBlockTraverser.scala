package com.effiban.scala2java.stubs

import com.effiban.scala2java.{BlockTraverser, JavaEmitter}

import scala.meta.{Init, Term}

class StubBlockTraverser(implicit javaEmitter: JavaEmitter) extends BlockTraverser{
  import javaEmitter._

  override def traverse(block: Term.Block, shouldReturnValue: Boolean, maybeInit: Option[Init] = None): Unit = {
    emitLine()

    val initStr = maybeInit.map(init => s"\nInput Init: $init").getOrElse("")
    val shouldReturnValueStr = if (shouldReturnValue) "\nShould return a value" else ""

    emitComment(
      s"""STUB BLOCK$initStr$shouldReturnValueStr
         |Scala Body:
         |$block""".stripMargin
    )
  }
}
