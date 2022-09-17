package effiban.scala2java.contexts

import effiban.scala2java.entities.JavaTreeType.JavaTreeType

import scala.meta.{Ctor, Init, Type}

case class TemplateBodyContext(javaScope: JavaTreeType,
                               maybeClassName: Option[Type.Name] = None,
                               maybePrimaryCtor: Option[Ctor.Primary] = None,
                               inits: List[Init] = Nil) extends JavaScopeAware
