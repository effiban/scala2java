package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.PatTupleRenderer

import scala.meta.Pat

trait PatTupleTraverser extends ScalaTreeTraverser[Pat.Tuple]

private[traversers] class PatTupleTraverserImpl(patTupleRenderer: PatTupleRenderer) extends PatTupleTraverser {

  // Pattern match tuple expression, no Java equivalent
  override def traverse(patternTuple: Pat.Tuple): Unit = {
    // TODO consider rewriting as a Java collection (depends on corresponding rewrite of the rest of the pattern-match clause)
    patTupleRenderer.render(patternTuple)
  }
}
