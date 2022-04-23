package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.{emit, emitLine}

import scala.meta.Mod
import scala.meta.Mod.Annot

object AnnotListTraverser {

  def traverseAnnotations(annotations: List[Annot], onSameLine: Boolean = false): Unit = {
    annotations.foreach(annotation => {
      AnnotTraverser.traverse(annotation)
      if (onSameLine) {
        emit(" ")
      } else {
        emitLine()
      }
    })
  }

  def traverseMods(mods: List[Mod], onSameLine: Boolean = false): Unit = {
    traverseAnnotations(annotations = mods.collect { case annot: Annot => annot },
      onSameLine = onSameLine)
  }

}
