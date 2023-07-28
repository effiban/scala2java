package io.github.effiban.scala2java.core.contexts

import io.github.effiban.scala2java.spi.entities.JavaScope.JavaScope

import scala.meta.{Ctor, Type}

case class TemplateContext(override val javaScope: JavaScope,
                           maybeClassName: Option[Type.Name] = None,
                           maybePrimaryCtor: Option[Ctor.Primary] = None) extends JavaScopeAware
