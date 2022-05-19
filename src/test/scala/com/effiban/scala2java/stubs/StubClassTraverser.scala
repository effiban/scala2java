package com.effiban.scala2java.stubs

import com.effiban.scala2java.{ClassTraverser, JavaEmitter}

import scala.meta.Defn

class StubClassTraverser(implicit javaEmitter: JavaEmitter) extends ClassTraverser {

  import javaEmitter._

  def traverse(classDef: Defn.Class): Unit = {
    emitComment(
      s"""STUB CLASS - Scala code:
        |$classDef""".stripMargin)
  }
}
