package io.github.effiban.scala2java.core.contexts

import io.github.effiban.scala2java.core.entities.JavaScope
import io.github.effiban.scala2java.core.entities.JavaScope.JavaScope

case class StatContext(override val javaScope: JavaScope = JavaScope.Unknown) extends JavaScopeAware
