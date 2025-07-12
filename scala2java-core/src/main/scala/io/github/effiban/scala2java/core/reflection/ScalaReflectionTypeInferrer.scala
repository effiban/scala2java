package io.github.effiban.scala2java.core.reflection

import io.github.effiban.scala2java.core.entities.TreeKeyedMap
import io.github.effiban.scala2java.core.entities.TypeSelects.ScalaAny
import io.github.effiban.scala2java.core.reflection.ScalaReflectionClassifier.isTrivialClassFullName
import io.github.effiban.scala2java.core.reflection.ScalaReflectionCreator.createTypeTagOf
import io.github.effiban.scala2java.core.reflection.ScalaReflectionInternalLookup.{findModuleSymbolOf, findSelfAndBaseTypeTagsOf, resolveAncestorTypeParamToTypeArg}
import io.github.effiban.scala2java.core.reflection.ScalaReflectionTransformer.{toClassSymbol, toScalaMetaType}

import scala.collection.immutable.ListMap
import scala.meta.transversers.Transformer
import scala.meta.{Term, Tree, Type, XtensionQuasiquoteType}
import scala.reflect.runtime.universe._

trait ScalaReflectionTypeInferrer {

  def inferScalaMetaTypeOf(qual: Term.Ref, name: Term.Name): Option[Type]

  def inferScalaMetaTypeOf(qual: Type.Ref, name: Term.Name): Option[Type]

  def inferScalaMetaTypeOf(qual: Type.Ref, qualArgs: List[Type], name: Term.Name): Option[Type]
}

object ScalaReflectionTypeInferrer extends ScalaReflectionTypeInferrer {

  def inferScalaMetaTypeOf(qual: Term.Ref, name: Term.Name): Option[Type] = {
    findModuleSymbolOf(qual.toString()) match {
      case Some(module) => inferScalaMetaTypeOf(module, name)
      case _ => None
    }
  }

  def inferScalaMetaTypeOf(qual: Type.Ref, name: Term.Name): Option[Type] = {
    toClassSymbol(qual) match {
      case Some(cls) => inferScalaMetaTypeOf(cls, name)
      case _ => None
    }
  }

  def inferScalaMetaTypeOf(qual: Type.Ref, qualArgs: List[Type], name: Term.Name): Option[Type] = {
    toClassSymbol(qual) match {
      case Some(qualCls) => qualCls.info.member(TermName(name.value)) match {
        case NoSymbol => None
        case member =>
          val owner = member.owner
          val ownerTypeTag = createTypeTagOf(owner.typeSignature)
          val placeholderTypeToSMQualArg = mapPlaceholderTypesTo(qualArgs)
          val qualWithPlaceholdersTypeTag = createTypeTagOf(qualCls.toType, placeholderTypeToSMQualArg.keySet.toList)
          val ownerTypeParamToQualPlaceholderTypeArg = resolveAncestorTypeParamToTypeArg(ownerTypeTag, qualWithPlaceholdersTypeTag)
          val smOwnerTypeParamToQualTypeArg = ownerTypeParamToQualPlaceholderTypeArg.map {
            case (typeParam, typeArg) => (Type.Name(typeParam.name.toString), placeholderTypeToSMQualArg.getOrElse(typeArg, ScalaAny))
          }
          val maybeSMMemberType = toScalaMetaType(member.typeSignature)
          maybeSMMemberType.map(replaceScalaMetaTypeParamsWithTypeArgs(_, smOwnerTypeParamToQualTypeArg))
      }
      case _ => None
    }
  }

  private def mapPlaceholderTypesTo(qualArgs: List[Type]) = {
    ListMap.from(
      qualArgs.zipWithIndex.flatMap {
        case (qualArg, idx) =>
          val smPlaceholderType = placeholderTypeAtIndex(idx)
          toClassSymbol(smPlaceholderType).map(placeholderCls => (placeholderCls.toType, qualArg))
      }
    )
  }

  private def placeholderTypeAtIndex(idx: Int) = {
    Type.Project(
      t"io.github.effiban.scala2java.core.reflection.ScalaReflectionTypeInferrer",
      Type.Name(s"Placeholder${idx + 1}")
    )
  }

  private def inferScalaMetaTypeOf(qualSym: Symbol, name: Term.Name): Option[Type] = {
    qualSym.info.member(TermName(name.value)) match {
      case NoSymbol => None
      case symbol => toScalaMetaType(symbol.typeSignature) match {
        case Some(smTypeSingleton: Type.Singleton) => inferScalaMetaBaseTypeOfSingleton(symbol, smTypeSingleton)
        case other => other
      }
    }
  }

  private def inferScalaMetaBaseTypeOfSingleton(singletonSymbol: Symbol, smTypeSingleton: Type.Singleton) = {
    findSelfAndBaseTypeTagsOf(createTypeTagOf(singletonSymbol.typeSignature))
      .filterNot(tag => isTrivialClassFullName(tag.tpe.typeSymbol.fullName)) match {
      case _ :: baseTag :: _ => toScalaMetaType(baseTag.tpe)
      case _ => Some(smTypeSingleton)
    }
  }

  private[reflection] def replaceScalaMetaTypeParamsWithTypeArgs(tpe: Type,
                                                                 typeParamToTypeArg: Map[Type.Name, Type]): Type = {
    new Transformer {
      override def apply(aTree: Tree): Tree = {
        aTree match {
          case tpe@(_: Type.Select | _: Type.Project) => tpe
          case typeName: Type.Name => TreeKeyedMap.get(typeParamToTypeArg, typeName)
            .getOrElse(ScalaAny)
          case other => super.apply(other)
        }
      }
    }.apply(tpe)
      .asInstanceOf[Type]
  }

  // The number of placeholder classes is according to the Scala maximum number of type params

  private class Placeholder1

  private class Placeholder2

  private class Placeholder3

  private class Placeholder4

  private class Placeholder5

  private class Placeholder6

  private class Placeholder7

  private class Placeholder8

  private class Placeholder9

  private class Placeholder10

  private class Placeholder11

  private class Placeholder12

  private class Placeholder13

  private class Placeholder14

  private class Placeholder15

  private class Placeholder16

  private class Placeholder17

  private class Placeholder18

  private class Placeholder19

  private class Placeholder20

  private class Placeholder21

  private class Placeholder22
}

