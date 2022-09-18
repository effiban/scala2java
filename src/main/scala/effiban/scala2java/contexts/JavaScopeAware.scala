package effiban.scala2java.contexts

import effiban.scala2java.entities.JavaScope.JavaScope

trait JavaScopeAware {
  val javaScope: JavaScope
}
