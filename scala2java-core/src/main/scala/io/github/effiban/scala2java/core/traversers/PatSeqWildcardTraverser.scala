package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.PatSeqWildcardRenderer

import scala.meta.Pat

trait PatSeqWildcardTraverser extends ScalaTreeTraverser[Pat.SeqWildcard]

class PatSeqWildcardTraverserImpl(patSeqWildcardRenderer: PatSeqWildcardRenderer) extends PatSeqWildcardTraverser {

  // Vararg in pattern match expression, e.g. `_*` in case List(xs @ _*).
  override def traverse(patternSeqWildcard: Pat.SeqWildcard): Unit = {
    //TODO consider transforming to Java guard
    patSeqWildcardRenderer.render(patternSeqWildcard)
  }
}
