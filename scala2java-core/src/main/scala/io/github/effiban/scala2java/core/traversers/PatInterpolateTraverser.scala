package io.github.effiban.scala2java.core.traversers

import scala.meta.Pat

trait PatInterpolateTraverser extends ScalaTreeTraverser1[Pat.Interpolate]

object PatInterpolateTraverser extends PatInterpolateTraverser {

  // Pattern interpolation e.g. r"Hello (.+)$name"
  override def traverse(patternInterpolation: Pat.Interpolate): Pat.Interpolate = {
    //TODO consider rewriting with Java Pattern and Matcher
    patternInterpolation
  }
}
