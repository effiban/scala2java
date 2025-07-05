package io.github.effiban.scala2java.core.reflection

import io.github.effiban.scala2java.core.reflection.ScalaReflectionAccess.RuntimeMirror
import io.github.effiban.scala2java.core.reflection.ScalaReflectionExtractor.dealiasedClassSymbolOf

import scala.reflect.runtime.universe._

private[reflection] object ScalaReflectionInternalLookup {

  def selfAndBaseClassesOf(cls: ClassSymbol): List[ClassSymbol] = {
    cls.baseClasses.flatMap(dealiasedClassSymbolOf)
  }

  def isTermMemberOf(symbol: Symbol, termName: String): Boolean = {
    findTermMemberOf(symbol, termName) match {
      case NoSymbol => false
      case _ => true
    }
  }

  def findInnerClassSymbolOf(outerClassSymbol: ClassSymbol, innerTypeName: TypeName): Symbol = {
    outerClassSymbol.info.decl(innerTypeName) match {
      case NoSymbol => outerClassSymbol.companion.info.decl(innerTypeName)
      case symbol => symbol
    }
  }

  def findModuleSymbolOf(qualifierName: String): Option[ModuleSymbol] = {
    scala.util.Try(RuntimeMirror.staticPackage(qualifierName))
      .orElse(scala.util.Try(RuntimeMirror.staticModule(qualifierName)))
      .toOption
  }

  def findTermMemberOf(owner: Symbol, termName: String): Symbol =
    owner.info.member(TermName(termName))

  def findModuleTypeMemberOf(module: ModuleSymbol, typeName: String): Symbol =
    module.typeSignature.decl(TypeName(typeName))
}
