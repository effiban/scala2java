package io.github.effiban.scala2java.core.reflection

import io.github.effiban.scala2java.core.reflection.ScalaReflectionAccess.RuntimeMirror
import io.github.effiban.scala2java.core.reflection.ScalaReflectionCreator.createTypeTagOf
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

  def findSelfAndBaseTypeTagsOf[T: TypeTag]: List[TypeTag[_]] = {
    val tpe = typeOf[T]

    // Get all supertypes by applying baseType to each base class symbol
    val baseTypes = tpe.baseClasses.map(sym => tpe.baseType(sym))

    // Convert each supertype into a TypeTag and extract its type arguments
    baseTypes.map(createTypeTagOf)
  }

  def resolveAncestorTypeParamToTypeArg(ancestorTypeTag: TypeTag[_], typeTag: TypeTag[_]): Map[TypeSymbol, Type] = {
    val theSelfAndBaseTypeTags = findSelfAndBaseTypeTagsOf(typeTag)
    theSelfAndBaseTypeTags.find(_.tpe.typeSymbol == ancestorTypeTag.tpe.typeSymbol) match {
      case Some(appliedAncestorTypeTag) => ancestorTypeTag.tpe.typeParams.indices
        .slice(0, appliedAncestorTypeTag.tpe.typeArgs.size)
        .map(idx => (ancestorTypeTag.tpe.typeParams(idx), appliedAncestorTypeTag.tpe.typeArgs(idx)))
        .map { case (typeParam: TypeSymbol, typeArg) => (typeParam, typeArg) }
        .toMap
      case None => Map.empty
    }
  }
}
