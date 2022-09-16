package effiban.scala2java.contexts

import effiban.scala2java.entities.JavaTreeType
import effiban.scala2java.entities.JavaTreeType.JavaTreeType

import scala.meta.Init

case class DefnDefContext(override val javaScope: JavaTreeType = JavaTreeType.Unknown,
                          maybeInit: Option[Init] = None) extends JavaScopeAware
