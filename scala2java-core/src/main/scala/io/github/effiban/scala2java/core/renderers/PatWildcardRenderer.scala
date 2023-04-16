package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.entities.TraversalConstants.JavaPlaceholder
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Pat

trait PatWildcardRenderer extends JavaTreeRenderer[Pat.Wildcard]

private[renderers] class PatWildcardRendererImpl(implicit javaWriter: JavaWriter) extends PatWildcardRenderer {

  import javaWriter._

  override def render(ignored: Pat.Wildcard): Unit = write(JavaPlaceholder)
}
