package io.github.effiban.scala2java.core.reflection

import io.github.effiban.scala2java.core.entities.TreeKeyedMap
import io.github.effiban.scala2java.core.entities.TypeSelects.ScalaAny
import io.github.effiban.scala2java.core.reflection.ScalaReflectionClassifier.isTrivialClassFullName
import io.github.effiban.scala2java.core.reflection.ScalaReflectionCreator.createTypeTagOf
import io.github.effiban.scala2java.core.reflection.ScalaReflectionInternalLookup.{findModuleSymbolOf, findSelfAndBaseTypeTagsOf, resolveAncestorTypeParamToTypeArg}
import io.github.effiban.scala2java.core.reflection.ScalaReflectionTransformer.{asScalaMetaTypeNameToType, toClassSymbol, toScalaMetaType, toTypeTag}

import scala.meta.transversers.Transformer
import scala.meta.{Term, Tree, Type}
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
          val maybeQualTypeTag = toTypeTag(qual, qualArgs)
          maybeQualTypeTag.flatMap(qualTypeTag => {
            val owner = member.owner
            val ownerTypeTag = createTypeTagOf(owner.typeSignature)
            val ownerTypeParamToQualTypeArg = resolveAncestorTypeParamToTypeArg(ownerTypeTag, qualTypeTag)
            val smOwnerTypeParamToQualTypeArg = asScalaMetaTypeNameToType(ownerTypeParamToQualTypeArg)
            val maybeSMMemberType = toScalaMetaType(member.typeSignature)
            maybeSMMemberType.map(replaceScalaMetaTypeParamsWithTypeArgs(_, smOwnerTypeParamToQualTypeArg))
          })
      }
      case _ => None
    }
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
}

