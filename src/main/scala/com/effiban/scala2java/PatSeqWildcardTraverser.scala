package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emitComment

import scala.meta.Pat

object PatSeqWildcardTraverser extends ScalaTreeTraverser[Pat.SeqWildcard] {

  // Vararg in pattern match expression, e.g. `_*` in case List(xs @ _*). Not translatable (?)
  override def traverse(patternSeqWildcard: Pat.SeqWildcard): Unit = {
    emitComment("_*")
  }
}
