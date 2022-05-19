package com.effiban.scala2java.stubs

import com.effiban.scala2java.{AnnotListTraverser, JavaEmitter}

import scala.meta.Mod
import scala.meta.Mod.Annot

class StubAnnotListTraverser(implicit javaEmitter: JavaEmitter) extends AnnotListTraverser {
  import javaEmitter._

  override def traverseAnnotations(annotations: List[Annot], onSameLine: Boolean = false): Unit = {
    annotations.foreach(annotation => {
      emit(annotation.toString())
      if (onSameLine) {
        emit(" ")
      } else {
        emitLine()
      }
    })
  }

  override def traverseMods(mods: List[Mod], onSameLine: Boolean = false): Unit = {
    traverseAnnotations(annotations = mods.collect { case annot: Annot => annot }, onSameLine = onSameLine)
  }
}
