package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Pat

trait PatExtractRenderer extends TreeRenderer[Pat.Extract]

private[renderers] class PatExtractRendererImpl(implicit javaWriter: JavaWriter) extends PatExtractRenderer {

  import javaWriter._

  override def render(patternExtractor: Pat.Extract): Unit = {
    writeComment(s"${patternExtractor.fun}(${patternExtractor.args.mkString(", ")})")
  }
}
