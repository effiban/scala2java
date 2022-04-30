package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emit

import scala.meta.Pat.Alternative

trait AlternativeTraverser extends ScalaTreeTraverser[Alternative]

private[scala2java] class AlternativeTraverserImpl(patTraverser: => PatTraverser) extends AlternativeTraverser {

  // Pattern match alternative, e.g. 2 | 3. In Java - separated by comma
  override def traverse(patternAlternative: Alternative): Unit = {
    patTraverser.traverse(patternAlternative.lhs)
    emit(", ")
    patTraverser.traverse(patternAlternative.rhs)
  }
}

object AlternativeTraverser extends AlternativeTraverserImpl(PatTraverser)
