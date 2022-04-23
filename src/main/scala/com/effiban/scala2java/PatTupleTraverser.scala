package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emitComment

import scala.meta.Pat

object PatTupleTraverser extends ScalaTreeTraverser[Pat.Tuple] {

  // Pattern match tuple expression, no Java equivalent
  override def traverse(patternTuple: Pat.Tuple): Unit = {
    emitComment(s"(${patternTuple.args.toString()})")
  }
}
