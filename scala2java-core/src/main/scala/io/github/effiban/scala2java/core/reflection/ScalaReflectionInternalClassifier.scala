package io.github.effiban.scala2java.core.reflection

import io.github.effiban.scala2java.core.reflection.ScalaReflectionTransformer.classSymbolOf

import scala.meta.Type
import scala.reflect.runtime.universe
import scala.reflect.runtime.universe._

private[reflection] object ScalaReflectionInternalClassifier {

  private[reflection] def isSingletonType(sym: Symbol): Boolean = isSingletonType(sym.typeSignature)

  private[reflection] def isSingletonType(tpe: universe.Type): Boolean = {
    tpe match {
      case _: SingleType => true // covers most singleton types
      case aType if aType.typeSymbol.isModuleClass => true // sometimes this is also true for objects
      case _ => false
    }
  }
}
