package io.github.effiban.scala2java.contexts

import io.github.effiban.scala2java.entities.JavaScope.JavaScope

trait JavaScopeAware {
  val javaScope: JavaScope
}
