package io.github.effiban.scala2java.core.reflection

import io.github.effiban.scala2java.core.reflection.ScalaReflectionExtractor.asClassSymbol
import io.github.effiban.scala2java.core.reflection.ScalaReflectionInternalLookup.{findInnerClassSymbolOf, findModuleSymbolOf}

import scala.meta.Type
import scala.reflect.runtime.universe._

object ScalaReflectionTransformer {

  def classSymbolOf(tpe: Type): Option[ClassSymbol] = tpe match {
    case Type.Apply(typeSelect: Type.Select, _) => classSymbolOf(typeSelect)
    case typeSelect: Type.Select => classSymbolOf(typeSelect)
    case Type.Project(tpe, name) => innerClassSymbolOf(tpe, name)
    case _ => None
  }

  private def classSymbolOf(typeSelect: Type.Select): Option[ClassSymbol] = {
    classSymbolOf(typeSelect.qual.toString(), typeSelect.name.value)
  }

  private def classSymbolOf(qualifierName: String, typeName: String): Option[ClassSymbol] = {
    findModuleSymbolOf(qualifierName)
      .map(module => module.typeSignature.decl(TypeName(typeName)))
      .flatMap(asClassSymbol)
  }

  private def innerClassSymbolOf(outerType: Type, innerName: Type.Name): Option[ClassSymbol] = {
    val innerTypeName = TypeName(innerName.value)
    classSymbolOf(outerType)
      .flatMap(outerClassSymbol => asClassSymbol(findInnerClassSymbolOf(outerClassSymbol, innerTypeName)))
  }
}
