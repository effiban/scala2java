package io.github.effiban.scala2java.core.enrichers.entities

import scala.meta.Decl

trait EnrichedDecl extends EnrichedStatWithJavaModifiers {
  override val stat: Decl
}
