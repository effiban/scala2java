package io.github.effiban.scala2java.core.enrichers.contexts

import io.github.effiban.scala2java.core.contexts.JavaScopeAware
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.spi.entities.JavaScope.JavaScope

import scala.meta.Type

case class TemplateStatEnrichmentContext(override val javaScope: JavaScope = JavaScope.Unknown,
                                         maybeClassName: Option[Type.Name] = None) extends JavaScopeAware
