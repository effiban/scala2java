package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Pat

trait PatInterpolateTraverser extends ScalaTreeTraverser[Pat.Interpolate]

private[traversers] class PatInterpolateTraverserImpl(implicit javaWriter: JavaWriter) extends PatInterpolateTraverser {

  import javaWriter._

  // Pattern interpolation e.g. r"Hello (.+)$name"
  override def traverse(patternInterpolation: Pat.Interpolate): Unit = {
    //TODO consider rewriting with Java Pattern and Matcher
    writeComment(patternInterpolation.toString())
  }
}
