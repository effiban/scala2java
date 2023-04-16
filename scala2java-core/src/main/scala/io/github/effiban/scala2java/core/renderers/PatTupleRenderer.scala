package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Pat

trait PatTupleRenderer extends TreeRenderer[Pat.Tuple]

private[renderers] class PatTupleRendererImpl(implicit javaWriter: JavaWriter) extends PatTupleRenderer {

  import javaWriter._

  override def render(patternTuple: Pat.Tuple): Unit = {
    writeComment(s"(${patternTuple.args.mkString(", ")})")
  }
}
