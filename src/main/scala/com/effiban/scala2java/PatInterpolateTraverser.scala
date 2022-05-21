package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emitComment

import scala.meta.Pat

trait PatInterpolateTraverser extends ScalaTreeTraverser[Pat.Interpolate]

private[scala2java] class PatInterpolateTraverserImpl(javaEmitter: JavaEmitter) extends PatInterpolateTraverser {

  // Pattern interpolation e.g. r"Hello (.+)$name"
  override def traverse(patternInterpolation: Pat.Interpolate): Unit = {
    //TODO consider rewriting with Java Pattern and Matcher
    emitComment(patternInterpolation.toString())
  }
}

object PatInterpolateTraverser extends PatInterpolateTraverserImpl(JavaEmitter)
