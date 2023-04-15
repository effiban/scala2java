package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.PatInterpolateRenderer

import scala.meta.Pat

trait PatInterpolateTraverser extends ScalaTreeTraverser[Pat.Interpolate]

private[traversers] class PatInterpolateTraverserImpl(patInterpolateRenderer: PatInterpolateRenderer) extends PatInterpolateTraverser {

  // Pattern interpolation e.g. r"Hello (.+)$name"
  override def traverse(patternInterpolation: Pat.Interpolate): Unit = {
    //TODO consider rewriting with Java Pattern and Matcher
    patInterpolateRenderer.render(patternInterpolation)
  }
}
