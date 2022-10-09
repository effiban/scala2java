package io.github.effiban.scala2java.contexts

import io.github.effiban.scala2java.entities.JavaScope
import io.github.effiban.scala2java.entities.JavaScope.JavaScope

case class StatContext(override val javaScope: JavaScope = JavaScope.Unknown) extends JavaScopeAware
