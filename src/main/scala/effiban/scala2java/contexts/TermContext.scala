package effiban.scala2java.contexts

import effiban.scala2java.entities.JavaTreeType
import effiban.scala2java.entities.JavaTreeType.JavaTreeType

case class TermContext(override val javaScope: JavaTreeType = JavaTreeType.Unknown) extends JavaScopeAware
