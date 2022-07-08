package effiban.scala2java.entities

sealed trait JavaScope

case object Class extends JavaScope

case object Interface extends JavaScope

case object Method extends JavaScope

case object Lambda extends JavaScope

case object NoOwner extends JavaScope
