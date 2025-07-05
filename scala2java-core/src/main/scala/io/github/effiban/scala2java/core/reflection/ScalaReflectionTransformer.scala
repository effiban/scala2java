package io.github.effiban.scala2java.core.reflection

import io.github.effiban.scala2java.core.reflection.ScalaReflectionExtractor.{asClassSymbol, finalResultTypeFullnameOf}
import io.github.effiban.scala2java.core.reflection.ScalaReflectionInternalClassifier.isSingletonType
import io.github.effiban.scala2java.core.reflection.ScalaReflectionInternalLookup.{findInnerClassSymbolOf, findModuleSymbolOf}

import scala.meta.{Term, Type, XtensionParseInputLike}
import scala.reflect.runtime.universe._

object ScalaReflectionTransformer {

  private[reflection] def toScalaMetaTermRef(member: Symbol): Option[Term.Ref] = {
    val maybeDealiasedFullName = member match {
      case termSymbol: TermSymbol =>
        if (isSingletonType(termSymbol)) {
          Some(finalResultTypeFullnameOf(termSymbol))
        } else {
          Some(termSymbol.fullName)
        }
      case _ => None
    }
    maybeDealiasedFullName.map(_.parse[Term].get.asInstanceOf[Term.Ref])
  }

  def asScalaMetaTypeRef(classSymbol: ClassSymbol): Option[Type.Ref] = {
    classSymbol.owner match {
      case owner if owner.isPackage =>
        val qualifier = owner.fullName.parse[Term].get.asInstanceOf[Term.Ref]
        Some(Type.Select(qualifier, Type.Name(classSymbol.name.toString)))
      case owner if owner.isClass =>
        val qualifier = owner.fullName.parse[Type].get
        Some(Type.Project(qualifier, Type.Name(classSymbol.name.toString)))
      case _ => None
    }
  }

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
