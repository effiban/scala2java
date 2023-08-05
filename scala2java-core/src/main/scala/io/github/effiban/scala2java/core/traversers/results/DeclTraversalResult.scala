package io.github.effiban.scala2java.core.traversers.results

import scala.meta.Decl

@deprecated
trait DeclTraversalResult extends StatWithJavaModifiersTraversalResult {
  override val tree: Decl
}
