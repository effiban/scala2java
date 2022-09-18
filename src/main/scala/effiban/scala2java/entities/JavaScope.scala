package effiban.scala2java.entities

object JavaScope extends Enumeration {
  type JavaScope = Value

  val Package,
  Class,
  Enum,
  Interface,
  MethodSignature,
  LambdaSignature,
  Block,
  Unknown = Value
}
