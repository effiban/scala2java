package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Pat.Alternative

trait AlternativeRenderer extends JavaTreeRenderer[Alternative]

private[renderers] class AlternativeRendererImpl(patRenderer: => PatRenderer)
                                                (implicit javaWriter: JavaWriter) extends AlternativeRenderer {

  import javaWriter._

  // Pattern match alternative, e.g. 2 | 3. In Java - separated by comma
  override def render(patternAlternative: Alternative): Unit = {
    patRenderer.render(patternAlternative.lhs)
    write(", ")
    patRenderer.render(patternAlternative.rhs)
  }
}
