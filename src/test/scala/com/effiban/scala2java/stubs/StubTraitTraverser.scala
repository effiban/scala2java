package com.effiban.scala2java.stubs

import com.effiban.scala2java.{JavaEmitter, TraitTraverser}

import scala.meta.Defn

class StubTraitTraverser(implicit javaEmitter: JavaEmitter) extends TraitTraverser {

  import javaEmitter._

  def traverse(traitDef: Defn.Trait): Unit = {
    emitComment(
      s"""STUB TRAIT - Scala code:
        |$traitDef""".stripMargin)
  }
}
