package effiban.scala2java.entities

import scala.meta.{Init, Type}

case class CtorContext(className: Type.Name, inits: List[Init])
