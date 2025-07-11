package io.github.effiban.scala2java.core.reflection

import io.github.effiban.scala2java.core.reflection.ScalaReflectionExtractor.{dealiasedClassSymbolOf, finalResultTypeArgsOf, finalResultTypeFullnameOf, finalResultTypeOf}
import io.github.effiban.scala2java.core.reflection.ScalaReflectionInternalClassifier.isSingletonType
import io.github.effiban.scala2java.core.reflection.ScalaReflectionInternalLookup.{findInnerClassSymbolOf, findModuleSymbolOf}

import scala.meta.{Term, Type, XtensionParseInputLike, XtensionQuasiquoteType}
import scala.reflect.runtime.universe
import scala.reflect.runtime.universe._

private[reflection] object ScalaReflectionTransformer {

  private final val ScalaMaxArity = 22

  def toScalaMetaTermRef(member: Symbol): Option[Term.Ref] = {
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

  def toScalaMetaTypeRef(symbol: Symbol): Option[Type.Ref] = {
    dealiasedClassSymbolOf(symbol).flatMap(classSymbol =>
      classSymbol.owner match {
        case owner if owner.isPackage =>
          val qualifier = owner.fullName.parse[Term].get.asInstanceOf[Term.Ref]
          Some(Type.Select(qualifier, Type.Name(classSymbol.name.toString)))
        case owner if owner.isClass =>
          val qualifier = owner.fullName.parse[Type].get
          Some(Type.Project(qualifier, Type.Name(classSymbol.name.toString)))
        case _ => None
      })
  }

  def toScalaMetaType(tpe: universe.Type): Option[Type] = {
    finalResultTypeOf(tpe) match {
      case sym if (1 to ScalaMaxArity).exists(n => sym == definitions.TupleClass(n)) =>
        val smTypeArgs = finalResultTypeArgsOf(tpe).map(
          typeArg => toScalaMetaType(typeArg).getOrElse(t"scala.Any")
        )
        Some(Type.Tuple(smTypeArgs))
      case sym => toScalaMetaTypeRef(sym)
    }
  }

  def toClassSymbol(tpe: Type): Option[ClassSymbol] = tpe match {
    case Type.Apply(typeSelect: Type.Select, _) => toClassSymbol(typeSelect)
    case typeSelect: Type.Select => toClassSymbol(typeSelect)
    case Type.Project(tpe, name) => innerClassSymbolOf(tpe, name)
    case _ => None
  }

  private def toClassSymbol(typeSelect: Type.Select): Option[ClassSymbol] = {
    toClassSymbolOf(typeSelect.qual.toString(), typeSelect.name.value)
  }

  private def toClassSymbolOf(qualifierName: String, typeName: String): Option[ClassSymbol] = {
    findModuleSymbolOf(qualifierName)
      .map(module => module.typeSignature.decl(TypeName(typeName)))
      .flatMap(dealiasedClassSymbolOf)
  }

  private def innerClassSymbolOf(outerType: Type, innerName: Type.Name): Option[ClassSymbol] = {
    val innerTypeName = TypeName(innerName.value)
    toClassSymbol(outerType)
      .flatMap(outerClassSymbol => dealiasedClassSymbolOf(findInnerClassSymbolOf(outerClassSymbol, innerTypeName)))
  }
}
