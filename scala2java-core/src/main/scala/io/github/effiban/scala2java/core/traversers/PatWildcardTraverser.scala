package io.github.effiban.scala2java.core.traversers

import scala.meta.Pat

trait PatWildcardTraverser extends ScalaTreeTraverser1[Pat.Wildcard]

object PatWildcardTraverser extends PatWildcardTraverser {

  // Wildcard in pattern match expression - renders as a Java placeholder (but not always supported)
  // When used alone it should be translated to "default" and this is handled by the parent traverser (CaseTraverser)
  override def traverse(patWildcard: Pat.Wildcard): Pat.Wildcard = patWildcard
}
