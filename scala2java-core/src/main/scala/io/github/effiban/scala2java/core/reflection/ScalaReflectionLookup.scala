package io.github.effiban.scala2java.core.reflection

import io.github.effiban.scala2java.core.reflection.ScalaReflectionExtractor.dealiasedClassSymbolOf
import io.github.effiban.scala2java.core.reflection.ScalaReflectionInternalLookup.{findModuleSymbolOf, findModuleTypeMemberOf}
import io.github.effiban.scala2java.core.reflection.ScalaReflectionTransformer.{asScalaMetaTypeRef, classSymbolOf, toScalaMetaTermRef}

import scala.meta.{Term, Type}
import scala.reflect.runtime.universe.{ClassSymbol, NoSymbol}

object ScalaReflectionLookup {

  def isTermMemberOf(typeRef: Type.Ref, termName: Term.Name): Boolean = {
    classSymbolOf(typeRef).exists(cls => ScalaReflectionInternalLookup.isTermMemberOf(cls, termName.value))
  }

  def isTermMemberOf(termRef: Term.Ref, termName: Term.Name): Boolean = {
    findModuleSymbolOf(termRef.toString()) match {
      case Some(module) => ScalaReflectionInternalLookup.isTermMemberOf(module, termName.value)
      case _ => false
    }
  }

  def selfAndBaseClassesOf(cls: ClassSymbol): List[ClassSymbol] = {
    ScalaReflectionInternalLookup.selfAndBaseClassesOf(cls)
  }

  def findModuleTermMemberOf(module: Term.Ref, termName: Term.Name): Option[Term.Ref] =
    findModuleSymbolOf(module.toString())
      .flatMap(module => ScalaReflectionInternalLookup.findTermMemberOf(module, termName.value) match {
        case NoSymbol => None
        case symbol => toScalaMetaTermRef(symbol)
      })

  def findAsScalaMetaTypeRef(module: Term.Ref, typeName: Type.Name): Option[Type.Ref] = {
    findModuleSymbolOf(module.toString()).flatMap(ownerModule => {
      dealiasedClassSymbolOf(findModuleTypeMemberOf(ownerModule, typeName.value))
        .flatMap(asScalaMetaTypeRef)
    })
  }
}
