package effiban.scala2java.contexts

import effiban.scala2java.entities.JavaScopeModifier.JavaScopeModifier
import effiban.scala2java.entities.JavaTreeType
import effiban.scala2java.entities.JavaTreeType.JavaTreeType

import scala.meta.Init

case class DefnDefContext(override val javaScope: JavaTreeType = JavaTreeType.Unknown,
                          override val javaScopeModifiers: Set[JavaScopeModifier] = Set.empty,
                          maybeInit: Option[Init] = None) extends JavaScopeAware
