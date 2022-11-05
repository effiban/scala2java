package io.github.effiban.scala2java.core.contexts

import io.github.effiban.scala2java.core.entities.JavaScope.JavaScope

import scala.meta.{Ctor, Name, Type}

case class TemplateContext(override val javaScope: JavaScope,
                           maybeClassName: Option[Type.Name] = None,
                           maybePrimaryCtor: Option[Ctor.Primary] = None,
                           override val permittedSubTypeNames: List[Name] = Nil)
  extends JavaScopeAware with PermittedSubTypeNamesHolder
