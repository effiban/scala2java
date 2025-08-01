package io.github.effiban.scala2java.core.reflection

import scala.reflect.runtime.universe._

private[reflection] object ScalaReflectionExtractor {

  def dealiasedClassSymbolOf(symbol: Symbol): Option[ClassSymbol] = {
    symbol match {
      case aClassSymbol: ClassSymbol => Some(aClassSymbol)
      // Handle the case of a Scala Type which is an alias to a Class
      case aTypeMember: TypeSymbol =>
        val resultType = finalResultTypeSymbolOf(aTypeMember.typeSignature)
        if (resultType.isClass) Some(resultType.asClass) else None
      case _ => None
    }
  }

  def finalResultTypeFullnameOf(sym: Symbol): String = {
    finalResultTypeFullnameOf(sym.typeSignature)
  }

  def finalResultTypeSymbolOf(sym: Symbol): Symbol = {
    finalResultTypeOf(sym).typeSymbol
  }

  def finalResultTypeFullnameOf(tpe: Type): String = {
    finalResultTypeSymbolOf(tpe).fullName
  }

  def finalResultTypeSymbolOf(tpe: Type): Symbol = {
    tpe.finalResultType.typeSymbol
  }

  def finalResultTypeArgsOf(tpe: Type): List[Type] = {
    tpe.finalResultType.typeArgs
  }

  def finalResultTypeOf(sym: Symbol): Type = {
    sym.typeSignature.finalResultType
  }
}
