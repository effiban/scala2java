package effiban.scala2java.entities

object JavaScope extends Enumeration {
  type JavaScope = Value

  val Package,
  Class,
  UtilityClass,
  Enum,
  Interface,
  MethodSignature,
  LambdaSignature,
  Block,
  Unknown = Value
}
