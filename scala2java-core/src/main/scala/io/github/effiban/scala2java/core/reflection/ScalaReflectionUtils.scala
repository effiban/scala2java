package io.github.effiban.scala2java.core.reflection

import scala.meta.{Term, Type}
import scala.reflect.runtime.universe._

object ScalaReflectionUtils {

  def isTermMemberOf(symbol: Symbol, termName: Term.Name): Boolean = {
    symbol.info.member(TermName(termName.value)) match {
      case NoSymbol => false
      case _ => true
    }
  }

  def isTypeMemberOf(symbol: Symbol, typeName: Type.Name): Boolean = {
    symbol.typeSignature.decl(TypeName(typeName.value)) match {
      case NoSymbol => false
      case _ => true
    }
  }
}
