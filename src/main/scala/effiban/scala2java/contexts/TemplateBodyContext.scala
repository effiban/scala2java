package effiban.scala2java.contexts

import scala.meta.{Ctor, Init, Type}

case class TemplateBodyContext(maybeClassName: Option[Type.Name] = None,
                               maybePrimaryCtor: Option[Ctor.Primary] = None,
                               inits: List[Init] = Nil)
