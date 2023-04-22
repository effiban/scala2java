package io.github.effiban.scala2java.core.traversers

import scala.meta.Pat

trait PatTupleTraverser extends ScalaTreeTraverser1[Pat.Tuple]

object PatTupleTraverser extends PatTupleTraverser {

  // Pattern match tuple expression, no Java equivalent
  override def traverse(patternTuple: Pat.Tuple): Pat.Tuple = {
    // TODO consider rewriting as a Java collection (depends on corresponding rewrite of the rest of the pattern-match clause)
    patternTuple
  }
}
