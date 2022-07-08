package effiban.scala2java.entities

object JavaScope extends Enumeration {
  type JavaScope = Value
  val Class, Interface, Method, Lambda, NoScope = Value
}
