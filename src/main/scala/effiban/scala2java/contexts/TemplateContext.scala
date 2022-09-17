package effiban.scala2java.contexts

import effiban.scala2java.entities.JavaTreeType
import effiban.scala2java.entities.JavaTreeType.JavaTreeType

import scala.meta.{Ctor, Type}

case class TemplateContext(override val javaScope: JavaTreeType = JavaTreeType.Unknown,
                           maybeClassName: Option[Type.Name] = None,
                           maybePrimaryCtor: Option[Ctor.Primary] = None) extends JavaScopeAware
