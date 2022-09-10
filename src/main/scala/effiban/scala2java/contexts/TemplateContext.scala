package effiban.scala2java.contexts

import scala.meta.{Ctor, Type}

case class TemplateContext(maybeClassName: Option[Type.Name] = None,
                           maybePrimaryCtor: Option[Ctor.Primary] = None)
