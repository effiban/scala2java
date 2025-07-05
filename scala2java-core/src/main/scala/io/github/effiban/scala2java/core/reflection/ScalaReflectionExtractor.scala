package io.github.effiban.scala2java.core.reflection

import scala.reflect.runtime.universe._

private[reflection] object ScalaReflectionExtractor {

  def dealiasedClassSymbolOf(symbol: Symbol): Option[ClassSymbol] = {
    symbol match {
      case aClassSymbol: ClassSymbol => Some(aClassSymbol)
      // Handle the case of a Scala Type which is an alias to a Class
      case aTypeMember: TypeSymbol =>
        val resultType = finalResultTypeOf(aTypeMember.typeSignature)
        if (resultType.isClass) Some(resultType.asClass) else None
      case _ => None
    }
  }

  def finalResultTypeFullnameOf(sym: Symbol): String = {
    finalResultTypeFullnameOf(sym.typeSignature)
  }

  def finalResultTypeFullnameOf(tpe: Type): String = {
    finalResultTypeOf(tpe).fullName
  }

  private def finalResultTypeOf(tpe: Type) = {
    tpe.finalResultType.typeSymbol
  }
}
