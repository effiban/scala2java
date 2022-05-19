package com.effiban.scala2java.stubs

import com.effiban.scala2java.{JavaEmitter, ObjectTraverser}

import scala.meta.Defn

class StubObjectTraverser(implicit javaEmitter: JavaEmitter) extends ObjectTraverser {

  import javaEmitter._

  def traverse(objectDef: Defn.Object): Unit = {
    emitComment(
      s"""STUB OBJECT - Scala code:
        |$objectDef""".stripMargin)
  }
}
