package com.effiban.scala2java.stubs

import com.effiban.scala2java.{AnnotTraverser, JavaEmitter}

import scala.meta.Mod.Annot

class StubAnnotTraverser(implicit javaEmitter: JavaEmitter) extends AnnotTraverser {
  import javaEmitter._

  override def traverse(annotation: Annot): Unit = emit(annotation.toString())
}
