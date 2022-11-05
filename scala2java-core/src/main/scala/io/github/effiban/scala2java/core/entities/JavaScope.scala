package io.github.effiban.scala2java.core.entities

object JavaScope extends Enumeration {
  type JavaScope = Value

  val Package,
  Sealed,
  Class,
  UtilityClass,
  Enum,
  Interface,
  MethodSignature,
  LambdaSignature,
  Block,
  Unknown = Value
}
