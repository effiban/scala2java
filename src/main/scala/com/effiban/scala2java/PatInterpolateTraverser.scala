package com.effiban.scala2java

import scala.meta.Pat

trait PatInterpolateTraverser extends ScalaTreeTraverser[Pat.Interpolate]

private[scala2java] class PatInterpolateTraverserImpl(implicit javaEmitter: JavaEmitter) extends PatInterpolateTraverser {
  import javaEmitter._

  // Pattern interpolation e.g. r"Hello (.+)$name"
  override def traverse(patternInterpolation: Pat.Interpolate): Unit = {
    //TODO consider rewriting with Java Pattern and Matcher
    emitComment(patternInterpolation.toString())
  }
}

object PatInterpolateTraverser extends PatInterpolateTraverserImpl()
