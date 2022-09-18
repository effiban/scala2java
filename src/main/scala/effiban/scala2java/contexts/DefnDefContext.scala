package effiban.scala2java.contexts

import effiban.scala2java.entities.JavaScope
import effiban.scala2java.entities.JavaScope.JavaScope

import scala.meta.Init

case class DefnDefContext(override val javaScope: JavaScope = JavaScope.Unknown,
                          maybeInit: Option[Init] = None) extends JavaScopeAware
