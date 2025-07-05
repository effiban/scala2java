package io.github.effiban.scala2java.core.reflection

import io.github.effiban.scala2java.core.reflection.ScalaReflectionExtractor.dealiasedClassSymbolOf
import io.github.effiban.scala2java.core.reflection.ScalaReflectionInternalLookup.{findModuleSymbolOf, findModuleTypeMemberOf}
import io.github.effiban.scala2java.core.reflection.ScalaReflectionTransformer.{toClassSymbol, toScalaMetaTermRef, toScalaMetaTypeRef}

import scala.meta.{Term, Type}
import scala.reflect.runtime.universe.NoSymbol

object ScalaReflectionLookup {

  def isTermMemberOf(typeRef: Type.Ref, termName: Term.Name): Boolean = {
    toClassSymbol(typeRef).exists(cls => ScalaReflectionInternalLookup.isTermMemberOf(cls, termName.value))
  }

  def isTermMemberOf(termRef: Term.Ref, termName: Term.Name): Boolean = {
    findModuleSymbolOf(termRef.toString()) match {
      case Some(module) => ScalaReflectionInternalLookup.isTermMemberOf(module, termName.value)
      case _ => false
    }
  }

  def findSelfAndBaseClassesOf(tpe: Type.Ref): List[Type.Ref] = {
    toClassSymbol(tpe)
      .toList
      .flatMap(ScalaReflectionInternalLookup.selfAndBaseClassesOf)
      .flatMap(toScalaMetaTypeRef)
      .distinctBy(_.structure)
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
        .flatMap(toScalaMetaTypeRef)
    })
  }
}
