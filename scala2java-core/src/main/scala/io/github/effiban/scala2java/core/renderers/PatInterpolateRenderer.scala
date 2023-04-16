package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Pat

trait PatInterpolateRenderer extends JavaTreeRenderer[Pat.Interpolate]

private[renderers] class PatInterpolateRendererImpl(implicit javaWriter: JavaWriter) extends PatInterpolateRenderer {

  import javaWriter._

  override def render(patternInterpolation: Pat.Interpolate): Unit = {
    writeComment(patternInterpolation.toString())
  }
}
