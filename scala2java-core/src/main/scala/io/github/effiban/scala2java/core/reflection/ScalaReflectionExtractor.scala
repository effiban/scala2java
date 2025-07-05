package io.github.effiban.scala2java.core.reflection

import scala.reflect.runtime.universe._

private[reflection] object ScalaReflectionExtractor {

  def asClassSymbol(symbol: Symbol): Option[ClassSymbol] = {
    symbol match {
      case aClassSymbol: ClassSymbol => Some(aClassSymbol)
      // Handle the case of a Scala Type which is an alias to a Class
      case aTypeMember: TypeSymbol =>
        val resultType = resultTypeOf(aTypeMember)
        if (resultType.isClass) Some(resultType.asClass) else None
      case _ => None
    }
  }

  private def resultTypeOf(aTypeMember: TypeSymbol) = {
    aTypeMember.typeSignature.resultType.typeSymbol
  }
}
