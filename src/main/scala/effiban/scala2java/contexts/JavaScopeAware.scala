package effiban.scala2java.contexts

import effiban.scala2java.entities.JavaScopeModifier.JavaScopeModifier
import effiban.scala2java.entities.JavaTreeType.JavaTreeType

trait JavaScopeAware {
  val javaScope: JavaTreeType
  val javaScopeModifiers: Set[JavaScopeModifier] = Set.empty
}
