package io.github.effiban.scala2java.traversers

import io.github.effiban.scala2java.writers.JavaWriter

import scala.meta.Pat

trait PatSeqWildcardTraverser extends ScalaTreeTraverser[Pat.SeqWildcard]

class PatSeqWildcardTraverserImpl(implicit javaWriter: JavaWriter) extends PatSeqWildcardTraverser {

  import javaWriter._

  // Vararg in pattern match expression, e.g. `_*` in case List(xs @ _*).
  override def traverse(patternSeqWildcard: Pat.SeqWildcard): Unit = {
    //TODO consider transforming to Java guard
    writeComment("...")
  }
}
