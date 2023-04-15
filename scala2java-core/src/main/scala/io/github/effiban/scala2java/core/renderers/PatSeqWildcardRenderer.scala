package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Pat

trait PatSeqWildcardRenderer extends TreeRenderer[Pat.SeqWildcard]

private[renderers] class PatSeqWildcardRendererImpl(implicit javaWriter: JavaWriter) extends PatSeqWildcardRenderer {

  import javaWriter._

  override def render(patternSeqWildcard: Pat.SeqWildcard): Unit = {
    writeComment("...")
  }
}
