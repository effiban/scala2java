package io.github.effiban.scala2java.core.reflection

import io.github.effiban.scala2java.core.reflection.ScalaReflectionClassifier.isTrivialClassFullName
import io.github.effiban.scala2java.core.reflection.ScalaReflectionCreator.createTypeTagOf
import io.github.effiban.scala2java.core.reflection.ScalaReflectionInternalLookup.{findModuleSymbolOf, findSelfAndBaseTypeTagsOf}
import io.github.effiban.scala2java.core.reflection.ScalaReflectionTransformer.{toClassSymbol, toScalaMetaType}

import scala.meta.{Term, Type}
import scala.reflect.runtime.universe._

trait ScalaReflectionTypeInferrer {

  def inferScalaMetaTypeOf(qual: Term.Ref, name: Term.Name): Option[Type]

  def inferScalaMetaTypeOf(qual: Type.Ref, name: Term.Name): Option[Type]
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
}

