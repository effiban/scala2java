package effiban.scala2java.contexts

import effiban.scala2java.entities.JavaTreeType.JavaTreeType

case class TemplateChildContext(override val javaScope: JavaTreeType, maybeCtorContext: Option[CtorContext] = None) extends JavaScopeAware
