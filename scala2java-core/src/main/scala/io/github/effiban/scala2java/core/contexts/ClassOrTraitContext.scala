package io.github.effiban.scala2java.core.contexts

import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.spi.entities.JavaScope.JavaScope

import scala.meta.Name

case class ClassOrTraitContext(override val javaScope: JavaScope = JavaScope.Unknown,
                               override val permittedSubTypeNames: List[Name] = Nil)
  extends JavaScopeAware with PermittedSubTypeNamesHolder
