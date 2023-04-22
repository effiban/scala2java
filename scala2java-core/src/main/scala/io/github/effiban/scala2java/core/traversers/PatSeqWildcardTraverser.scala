package io.github.effiban.scala2java.core.traversers

import scala.meta.Pat

trait PatSeqWildcardTraverser extends ScalaTreeTraverser1[Pat.SeqWildcard]

object PatSeqWildcardTraverser extends PatSeqWildcardTraverser {

  // Vararg in pattern match expression, e.g. `_*` in case List(xs @ _*).
  override def traverse(patternSeqWildcard: Pat.SeqWildcard): Pat.SeqWildcard = {
    //TODO consider transforming to Java guard
    patternSeqWildcard
  }
}
