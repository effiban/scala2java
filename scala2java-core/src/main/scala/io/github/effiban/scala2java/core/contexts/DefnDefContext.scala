package io.github.effiban.scala2java.core.contexts

import io.github.effiban.scala2java.core.entities.JavaScope
import io.github.effiban.scala2java.core.entities.JavaScope.JavaScope

import scala.meta.Init

case class DefnDefContext(override val javaScope: JavaScope = JavaScope.Unknown,
                          maybeInit: Option[Init] = None) extends JavaScopeAware
