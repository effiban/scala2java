package io.github.effiban.scala2java.spi.entities

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
