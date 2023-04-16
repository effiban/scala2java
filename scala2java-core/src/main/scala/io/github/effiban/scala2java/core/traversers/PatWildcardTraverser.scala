package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.PatWildcardRenderer

import scala.meta.Pat

trait PatWildcardTraverser extends ScalaTreeTraverser[Pat.Wildcard]

private[traversers] class PatWildcardTraverserImpl(patWildcardRenderer: PatWildcardRenderer) extends PatWildcardTraverser {

  // Wildcard in pattern match expression - renders as a Java placeholder (but not always supported)
  // When used alone it should be translated to "default" and this is handled by the parent traverser (CaseTraverser)
  override def traverse(patWildcard: Pat.Wildcard): Unit = patWildcardRenderer.render(patWildcard)
}
