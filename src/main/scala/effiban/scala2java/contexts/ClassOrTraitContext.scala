package effiban.scala2java.contexts

import effiban.scala2java.entities.JavaScope
import effiban.scala2java.entities.JavaScope.JavaScope

case class ClassOrTraitContext(override val javaScope: JavaScope = JavaScope.Unknown,
                               override val javaPermittedSubTypeNames: List[String] = Nil)
  extends JavaScopeAware with PermittedSubTypeNamesHolder
