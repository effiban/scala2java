package io.github.effiban.scala2java.core.contexts

import io.github.effiban.scala2java.core.entities.JavaScope.JavaScope

trait JavaScopeAware {
  val javaScope: JavaScope
}
