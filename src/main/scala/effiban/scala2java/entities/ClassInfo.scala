package effiban.scala2java.entities

import scala.meta.{Ctor, Type}

private[scala2java] case class ClassInfo(className: Type.Name,
                                         maybePrimaryCtor: Option[Ctor.Primary] = None)