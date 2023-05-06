package io.github.effiban.scala2java.core.traversers

import scala.meta.Pat.Alternative

trait AlternativeTraverser extends ScalaTreeTraverser1[Alternative]

private[traversers] class AlternativeTraverserImpl(patTraverser: => PatTraverser) extends AlternativeTraverser {

  // Pattern match alternative, e.g. 2 | 3. In Java - separated by comma
  override def traverse(patternAlternative: Alternative): Alternative = {
    val traversedLhs = patTraverser.traverse(patternAlternative.lhs)
    val traversedRhs = patTraverser.traverse(patternAlternative.rhs)
    Alternative(traversedLhs, traversedRhs)
  }
}
