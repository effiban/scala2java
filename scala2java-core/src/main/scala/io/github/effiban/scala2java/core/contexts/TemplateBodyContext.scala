package io.github.effiban.scala2java.core.contexts

import io.github.effiban.scala2java.spi.entities.JavaScope.JavaScope

import scala.meta.{Ctor, Init, Type}

case class TemplateBodyContext(javaScope: JavaScope,
                               maybeClassName: Option[Type.Name] = None,
                               maybePrimaryCtor: Option[Ctor.Primary] = None,
                               inits: List[Init] = Nil) extends JavaScopeAware
