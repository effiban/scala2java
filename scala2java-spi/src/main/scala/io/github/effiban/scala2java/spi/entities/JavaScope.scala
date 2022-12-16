package io.github.effiban.scala2java.spi.entities

/** Indicates the lexical scope that a given tree will have in the generated Java code.
 * Examples:
 *   - A class data member will have `Class` scope
 *   - A method parameter will have `MethodSignature` scope
 *   - A local variable will have `Block` scope
 */
object JavaScope extends Enumeration {
  type JavaScope = Value

  /** The scope of a package */
  val Package,
  /** The scope of a hierarchy of sealed types */
  Sealed,
  /** The scope of a regular class (having at least one instance method) */
  Class,
  /** The scope of a utility class (a singleton with static methods only) */
  UtilityClass,
  /** The scope of an enum */
  Enum,
  /** The scope of an interface */
  Interface,
  /** The scope of a method signature (any child tree of a method except for its body) */
  MethodSignature,
  /** The scope of a lambda signature (any child tree of a lambda except for its body) */
  LambdaSignature,
  /** The scope of a block of code (such as a method body, lambda body or any block in curly braces) */
  Block,
  /** The default scope in case none of the above apply  */
  Unknown = Value
}
