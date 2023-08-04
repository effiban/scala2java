package io.github.effiban.scala2java.core.enrichers.contexts

import io.github.effiban.scala2java.core.contexts.JavaScopeAware
import io.github.effiban.scala2java.spi.entities.JavaScope.JavaScope

import scala.meta.Type

case class CtorSecondaryEnrichmentContext(override val javaScope: JavaScope, className: Type.Name) extends JavaScopeAware
