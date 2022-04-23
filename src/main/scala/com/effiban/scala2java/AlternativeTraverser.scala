package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emit

import scala.meta.Pat.Alternative

object AlternativeTraverser extends ScalaTreeTraverser[Alternative] {

  // Pattern match alternative, e.g. 2 | 3. In Java - separated by comma
  override def traverse(patternAlternative: Alternative): Unit = {
    GenericTreeTraverser.traverse(patternAlternative.lhs)
    emit(", ")
    GenericTreeTraverser.traverse(patternAlternative.rhs)
  }
}
