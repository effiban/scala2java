package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.entities.TraversalConstants.JavaPlaceholder
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Pat

trait PatWildcardTraverser extends ScalaTreeTraverser[Pat.Wildcard]

private[traversers] class PatWildcardTraverserImpl(implicit javaWriter: JavaWriter) extends PatWildcardTraverser {

  import javaWriter._

  // Wildcard in pattern match expression - translates to Java placeholder (but not always supported)
  // When used alone it should be translated to "default" and this is handled by the parent traverser (CaseTraverser)
  override def traverse(ignored: Pat.Wildcard): Unit = write(JavaPlaceholder)
}
