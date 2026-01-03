package io.github.effiban.scala2java.core.reflection

import io.github.effiban.scala2java.core.entities.TypeSelects.ScalaAny
import io.github.effiban.scala2java.core.reflection.ScalaReflectionExtractor._
import io.github.effiban.scala2java.core.reflection.ScalaReflectionInternalClassifier._
import io.github.effiban.scala2java.core.reflection.ScalaReflectionInternalLookup.{findInnerClassSymbolOf, findModuleSymbolOf}
import io.github.effiban.scala2java.spi.entities.PartialDeclDef

import scala.meta.{Term, Type, XtensionParseInputLike}
import scala.reflect.runtime.universe
import scala.reflect.runtime.universe._

private[reflection] object ScalaReflectionTransformer {

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
    symbol match {
      case sym if sym.isParameter => Some(Type.Name(sym.name.toString))
      case sym: Symbol => toScalaMetaTypeRefFromClass(sym)
      case _ => None
    }
  }

  def toScalaMetaType(tpe: universe.Type): Option[Type] = {
    if (isSingletonType(tpe)) toScalaMetaTypeSingleton(tpe) else toScalaMetaTypeNonSingleton(tpe)
  }

  def toClassSymbol(tpe: Type): Option[ClassSymbol] = tpe match {
    case Type.Apply(typeSelect: Type.Select, _) => toClassSymbol(typeSelect)
    case typeSelect: Type.Select => toClassSymbol(typeSelect)
    case Type.Project(tpe, name) => innerClassSymbolOf(tpe, name)
    case Type.Singleton(term: Term.Ref) =>
      findModuleSymbolOf(term.toString())
        .map(module => module.typeSignature.typeSymbol)
        .flatMap(dealiasedClassSymbolOf)
    case _ => None
  }

  def toScalaMetaPartialDeclDef(method: MethodSymbol): PartialDeclDef = {
    val maybeSMParamTypeLists = method.paramLists.map { params =>
      params.map(param => toScalaMetaType(param.typeSignature))
    }
    val maybeSMReturnType = toScalaMetaType(finalResultTypeOf(method))
    PartialDeclDef(
      maybeParamTypeLists = maybeSMParamTypeLists,
      maybeReturnType = maybeSMReturnType
    )
  }

  private def toScalaMetaTypeRefFromClass(symbol: Symbol): Option[Type.Ref] = {
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

  private def toScalaMetaTypeSingleton(tpe: universe.Type) = {
    val maybeScalaMetaObjRef = finalResultTypeFullnameOf(tpe).parse[Term].toOption.map(_.asInstanceOf[Term.Ref])
    maybeScalaMetaObjRef.map(Type.Singleton(_))
  }

  private def toScalaMetaTypeNonSingleton(tpe: universe.Type) = {
    finalResultTypeSymbolOf(tpe) match {
      case sym if isFunctionType(sym) => toScalaMetaTypeFunction(tpe)
      case sym if isTupleType(sym) => toScalaMetaTypeTuple(tpe)
      case sym if isByNameParamType(sym) => toScalaMetaTypeByName(tpe)
      case sym if isRepeatedParamType(sym) => toScalaMetaTypeRepeated(tpe)
      case sym =>
        val maybeSMType = toScalaMetaTypeRef(sym)
        (maybeSMType, tpe.finalResultType.typeArgs) match {
          case (None, _) => None
          case (_, Nil) => maybeSMType
          case (Some(scalaMetaType), targs) => toScalaMetaTypeApply(scalaMetaType, targs)
        }
    }
  }

  private def toScalaMetaTypeByName(tpe: universe.Type) = {
    byNameInnerTypeOf(tpe)
      .flatMap(innerType => toScalaMetaType(innerType).map(Type.ByName(_)))
  }

  private def toScalaMetaTypeFunction(tpe: universe.Type) = {
    val smTypeArgs = tpe.finalResultType.typeArgs.map(
      typeArg => toScalaMetaType(typeArg).getOrElse(ScalaAny)
    )
    smTypeArgs.lastOption.map(smResultTypeArg => {
      val smParamTypeArgs = smTypeArgs.dropRight(1)
      Type.Function(smParamTypeArgs, smResultTypeArg)
    })
  }

  private def toScalaMetaTypeTuple(tpe: universe.Type) = {
    val smTypeArgs = finalResultTypeArgsOf(tpe).map(
      typeArg => toScalaMetaType(typeArg).getOrElse(ScalaAny)
    )
    smTypeArgs match {
      case Nil => None
      case smTypeArgs => Some(Type.Tuple(smTypeArgs))
    }
  }

  private def toScalaMetaTypeRepeated(tpe: universe.Type) = {
    finalResultTypeArgsOf(tpe).headOption
      .map(smTypeArg => toScalaMetaType(smTypeArg).getOrElse(ScalaAny))
      .map(Type.Repeated(_))
  }


  private def toScalaMetaTypeApply(scalaMetaType: Type.Ref, targs: List[universe.Type]) = {
    val scalaMetaTargs = targs.map(targ => toScalaMetaType(targ).getOrElse(ScalaAny))
    Some(Type.Apply(scalaMetaType, scalaMetaTargs))
  }
}
