package io.github.effiban.scala2java.core.reflection

import scala.reflect.runtime.universe
import scala.reflect.runtime.universe._

private[reflection] object ScalaReflectionInternalClassifier {

  private final val ScalaMaxArity = 22

  def isSingletonType(sym: Symbol): Boolean = isSingletonType(sym.typeSignature)

  def isSingletonType(tpe: universe.Type): Boolean = {
    tpe match {
      case _: SingleType => true // covers most singleton types
      case aType if aType.typeSymbol.isModuleClass => true // sometimes this is also true for objects
      case _ => false
    }
  }

  def isTupleType(sym: Symbol): Boolean = {
    (1 to ScalaMaxArity).exists(n => sym == definitions.TupleClass(n))
  }

  def isFunctionType(sym: Symbol): Boolean = {
    (0 to ScalaMaxArity).exists(n => sym == definitions.FunctionClass(n))
  }

  def isByNameParamType(sym: Symbol): Boolean = sym == definitions.ByNameParamClass

  def isRepeatedParamType(sym: Symbol): Boolean = sym == definitions.RepeatedParamClass
}
