package effiban.scala2java.contexts

import effiban.scala2java.entities.JavaTreeType.JavaTreeType

trait JavaScopeAware {
  val javaScope: JavaTreeType
}
