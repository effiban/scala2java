package io.github.effiban.scala2java.core.reflection

import io.github.effiban.scala2java.core.reflection.ScalaReflectionExtractor.asClassSymbol
import io.github.effiban.scala2java.core.reflection.ScalaReflectionInternalLookup.{findModuleSymbolOf, findModuleTypeMemberOf, findTermMemberOf}
import io.github.effiban.scala2java.core.reflection.ScalaReflectionTransformer.classSymbolOf

import scala.meta.{Term, Type, XtensionParseInputLike}
import scala.reflect.runtime.universe._

object ScalaReflectionUtils {

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

  def selfAndBaseClassesOf(cls: ClassSymbol): List[ClassSymbol] = {
    ScalaReflectionInternalLookup.selfAndBaseClassesOf(cls)
  }

  def isTermMemberOf(typeRef: Type.Ref, termName: Term.Name): Boolean = {
    classSymbolOf(typeRef).exists(cls => ScalaReflectionInternalLookup.isTermMemberOf(cls, termName.value))
  }

  def isTermMemberOf(termSelect: Term.Select, termName: Term.Name): Boolean = {
    findModuleSymbolOf(termSelect.toString()) match {
      case Some(module) => ScalaReflectionInternalLookup.isTermMemberOf(module, termName.value)
      case _ => false
    }
  }

  def findAndDealiasAsScalaMetaTermRef(moduleTerm: Term.Ref, termName: Term.Name): Option[Term.Ref] = {
    findModuleSymbolOf(moduleTerm.toString()).flatMap(ownerModule => {
      val member = findTermMemberOf(ownerModule, termName.value)
      val maybeDealiasedFullName = member match {
        case termSymbol: TermSymbol =>
          val memberTypeFullName = member.typeSignature.toString
          if (memberTypeFullName.endsWith(".type") && !termSymbol.isJava) {
            // This indicates a member which is an alias to a Scala object
            Some(s"scala.${memberTypeFullName.stripPrefix("scala.").stripSuffix(".type")}")
          } else {
            Some(termSymbol.fullName)
          }
        case _ => None
      }
      maybeDealiasedFullName.map(_.parse[Term].get.asInstanceOf[Term.Ref])
    })
  }

  def findAsScalaMetaTypeRef(module: Term.Ref, typeName: Type.Name): Option[Type.Ref] = {
    findModuleSymbolOf(module.toString()).flatMap(ownerModule => {
      asClassSymbol(findModuleTypeMemberOf(ownerModule, typeName.value))
        .flatMap(asScalaMetaTypeRef)
    })
  }
}
