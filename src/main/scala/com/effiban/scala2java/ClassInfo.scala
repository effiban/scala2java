package com.effiban.scala2java

import scala.meta.{Ctor, Type}

private[scala2java] case class ClassInfo(className: Type.Name,
                                         maybeExplicitPrimaryCtor: Option[Ctor.Primary] = None)
