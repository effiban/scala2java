package io.github.effiban.scala2java.core.contexts

import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.spi.entities.JavaScope.JavaScope

case class ClassOrTraitContext(override val javaScope: JavaScope = JavaScope.Unknown) extends JavaScopeAware
