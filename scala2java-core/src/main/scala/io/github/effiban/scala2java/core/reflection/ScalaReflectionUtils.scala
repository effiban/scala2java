package io.github.effiban.scala2java.core.reflection

import io.github.effiban.scala2java.core.reflection.ScalaReflectionAccess.RuntimeMirror
import io.github.effiban.scala2java.core.reflection.ScalaReflectionExtractor.asClassSymbol
import io.github.effiban.scala2java.core.reflection.ScalaReflectionTransformer.{classSymbolOf, findModuleSymbolOf}

import scala.annotation.tailrec
import scala.meta.{Defn, Member, Pkg, Term, Type, XtensionParseInputLike}
import scala.reflect.runtime.universe._

object ScalaReflectionUtils {

  private val TrivialClassFullNames =
    Set(
      "java.lang.Object",
      "java.io.Serializable",
      "scala.Any",
      "scala.AnyRef",
      "scala.AnyVal"
  )

  def symbolOf(memberPath: List[Member]): Option[Symbol] = {
    memberPath match {
      case (pkg: Pkg) :: members =>
        symbolOf(RuntimeMirror.staticPackage(pkg.ref.toString()), members)
      case _ => None
    }
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

  def selfAndBaseClassesOf(cls: ClassSymbol): List[ClassSymbol] = {
    cls.baseClasses.flatMap(asClassSymbol)
  }

  def isTermMemberOf(typeRef: Type.Ref, termName: Term.Name): Boolean = {
    classSymbolOf(typeRef).exists(cls => isTermMemberOf(cls, termName))
  }

  def isTermMemberOf(termSelect: Term.Select, termName: Term.Name): Boolean = {
    moduleSymbolOf(termSelect.toString()) match {
      case Some(module) => isTermMemberOf(module, termName)
      case _ => false
    }
  }

  def isTermMemberOf(symbol: Symbol, termName: Term.Name): Boolean = {
    symbol.info.member(TermName(termName.value)) match {
      case NoSymbol => false
      case _ => true
    }
  }

  def findAndDealiasAsScalaMetaTermRef(moduleTerm: Term.Ref, termName: Term.Name): Option[Term.Ref] = {
    findModuleSymbolOf(moduleTerm.toString()).flatMap(ownerModule => {
      val member = ownerModule.info.member(TermName(termName.value))
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
      asClassSymbol(ownerModule.typeSignature.decl(TypeName(typeName.value)))
        .flatMap(asScalaMetaTypeRef)
    })
  }

  def isNonTrivialEmptyType(typeRef: Type.Ref): Boolean = {
    classSymbolOf(typeRef) match {
      case None => false
      case Some(cls) => isNonTrivialEmptyClass(cls)
    }
  }

  private def isNonTrivialEmptyClass(cls: ClassSymbol) = {
    isNonTrivialClassFullName(cls.fullName) &&
      hasTrivialDeclarationsOnly(cls) &&
      hasTrivialBaseClassesOnly(cls)
  }

  private def hasTrivialDeclarationsOnly(cls: ClassSymbol) = {
    cls.info.decls.forall {
      case m: MethodSymbol => m.isConstructor && m.paramLists.flatten.isEmpty
      case _ => false
    }
  }

  private def hasTrivialBaseClassesOnly(cls: ClassSymbol) = {
    val baseClassesExcludingSelf = cls.baseClasses
      .slice(1, cls.baseClasses.size)
      .map(_.fullName)

    baseClassesExcludingSelf.forall(isTrivialClassFullName)
  }

  @tailrec
  private def symbolOf(symbol: Symbol, memberPath: List[Member]): Option[Symbol] = {
    (symbol, memberPath) match {
      case (symbol: Symbol, (classOrTrait@(_: Defn.Class | _: Defn.Trait)) :: members) =>
        symbol.typeSignature.decl(TypeName(classOrTrait.name.value)) match {
          case NoSymbol => None
          case aSymbol => symbolOf(aSymbol, members)
        }
      case (symbol: Symbol, (obj: Defn.Object) :: members) =>
        symbol.info.member(TermName(obj.name.value)) match {
          case NoSymbol => None
          case aSymbol => symbolOf(aSymbol, members)
        }
      case (symbol: Symbol, Nil) => Some(symbol)
      case _ => None
    }
  }

  private def moduleSymbolOf(qualifierName: String) = {
    scala.util.Try(RuntimeMirror.staticPackage(qualifierName))
      .orElse(scala.util.Try(RuntimeMirror.staticModule(qualifierName)))
      .toOption
  }

  private def isNonTrivialClassFullName(name: String) = !isTrivialClassFullName(name)


  private def isTrivialClassFullName(name: String) = TrivialClassFullNames.contains(name)

}
