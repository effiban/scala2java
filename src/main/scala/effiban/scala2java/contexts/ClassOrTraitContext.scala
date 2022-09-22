package effiban.scala2java.contexts

import effiban.scala2java.entities.JavaScope
import effiban.scala2java.entities.JavaScope.JavaScope

import scala.meta.Name

case class ClassOrTraitContext(override val javaScope: JavaScope = JavaScope.Unknown,
                               override val permittedSubTypeNames: List[Name] = Nil)
  extends JavaScopeAware with PermittedSubTypeNamesHolder
