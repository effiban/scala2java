package effiban.scala2java

sealed trait JavaOwnerContext

case object Class extends JavaOwnerContext

case object Interface extends JavaOwnerContext

case object Method extends JavaOwnerContext

case object Lambda extends JavaOwnerContext

case object NoOwner extends JavaOwnerContext
