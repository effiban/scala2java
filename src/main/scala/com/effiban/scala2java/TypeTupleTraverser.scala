package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emitComment

import scala.meta.Type

trait TypeTupleTraverser extends ScalaTreeTraverser[Type.Tuple]

object TypeTupleTraverser extends TypeTupleTraverser {

  //tuple as type, e.g. x: (Int, String).
  override def traverse(tupleType: Type.Tuple): Unit = {
    // TODO if only 2 params, can be translated into Java Map.Entry or Apache Pair
    emitComment(tupleType.toString())
  }
}
