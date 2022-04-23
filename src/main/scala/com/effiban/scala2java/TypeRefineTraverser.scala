package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emitComment

import scala.meta.Type

object TypeRefineTraverser extends ScalaTreeTraverser[Type.Refine] {

  // A {def f: Int }
  def traverse(refinedType: Type.Refine): Unit = {
    refinedType.tpe.foreach(GenericTreeTraverser.traverse)
    // TODO maybe convert to Java type with inheritance
    emitComment(s" ${refinedType.stats.toString()}")
  }
}
