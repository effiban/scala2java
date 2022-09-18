package effiban.scala2java.contexts

import effiban.scala2java.entities.JavaScopeModifier.JavaScopeModifier
import effiban.scala2java.entities.JavaTreeType.JavaTreeType

import scala.meta.{Ctor, Type}

case class TemplateContext(override val javaScope: JavaTreeType,
                           override val javaScopeModifiers: Set[JavaScopeModifier] = Set.empty,
                           maybeClassName: Option[Type.Name] = None,
                           maybePrimaryCtor: Option[Ctor.Primary] = None) extends JavaScopeAware
