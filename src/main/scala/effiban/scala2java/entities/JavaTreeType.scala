package effiban.scala2java.entities

object JavaTreeType extends Enumeration {
  type JavaTreeType = Value

  val Package,
  Class,
  Interface,
  Method,
  Lambda,
  Variable,
  Parameter,
  Unknown = Value
}
