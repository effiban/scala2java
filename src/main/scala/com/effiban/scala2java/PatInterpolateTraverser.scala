package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emitComment

import scala.meta.Pat

object PatInterpolateTraverser extends ScalaTreeTraverser[Pat.Interpolate] {

  // Pattern interpolation e.g. r"Hello (.+)$name"
  override def traverse(patternInterpolation: Pat.Interpolate): Unit = {
    // TODO consider rewriting with Java Pattern and Matcher
    emitComment(patternInterpolation.toString())
  }
}
