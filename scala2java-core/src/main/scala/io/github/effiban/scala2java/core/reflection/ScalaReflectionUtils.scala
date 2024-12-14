package io.github.effiban.scala2java.core.reflection

import io.github.effiban.scala2java.core.entities.ReflectedEntities.RuntimeMirror

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

  def classSymbolOf(tpe: Type): Option[ClassSymbol] = tpe match {
    case Type.Apply(typeSelect: Type.Select, _) => classSymbolOf(typeSelect)
    case typeSelect: Type.Select => classSymbolOf(typeSelect)
    case Type.Project(tpe, name) => innerClassSymbolOf(tpe, name)
    case _ => None
  }

  def classSymbolOf(memberPath: List[Member]): Option[ClassSymbol] = {
    symbolOf(memberPath) match {
      case Some(symbol) => asClassSymbol(symbol)
      case _ => None
    }
  }

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

  def isTermMemberOf(symbol: Symbol, termName: Term.Name): Boolean = {
    symbol.info.member(TermName(termName.value)) match {
      case NoSymbol => false
      case _ => true
    }
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

  def isTermMemberOfCompanionOf(symbol: Symbol, termName: Term.Name): Boolean = {
    symbol.info.companion.member(TermName(termName.value)) match {
      case NoSymbol => false
      case _ => true
    }
  }

  def isTermMemberOfCompanionOf(typeRef: Type.Ref, termName: Term.Name): Boolean = {
    classSymbolOf(typeRef).exists(cls => isTermMemberOfCompanionOf(cls, termName))
  }

  def isTypeMemberOf(symbol: Symbol, typeName: Type.Name): Boolean = {
    symbol.typeSignature.decl(TypeName(typeName.value)) match {
      case NoSymbol => false
      case _ => true
    }
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

  private def classSymbolOf(typeSelect: Type.Select): Option[ClassSymbol] = {
    classSymbolOf(typeSelect.qual.toString(), typeSelect.name.value)
  }

  private def classSymbolOf(qualifierName: String, typeName: String): Option[ClassSymbol] = {
    moduleSymbolOf(qualifierName)
      .map(module => module.typeSignature.decl(TypeName(typeName)))
      .flatMap(asClassSymbol)
  }

  private def innerClassSymbolOf(outerType: Type, innerName: Type.Name): Option[ClassSymbol] = {
    val innerTypeName = TypeName(innerName.value)
    classSymbolOf(outerType)
      .flatMap(outerClassSymbol => asClassSymbol(innerClassSymbolOf(outerClassSymbol, innerTypeName)))
  }

  private def innerClassSymbolOf(outerClassSymbol: ClassSymbol, innerTypeName: TypeName): Symbol = {
    outerClassSymbol.info.decl(innerTypeName) match {
      case NoSymbol => outerClassSymbol.companion.info.decl(innerTypeName)
      case symbol => symbol
    }
  }

  private def moduleSymbolOf(qualifierName: String) = {
    scala.util.Try(RuntimeMirror.staticPackage(qualifierName))
      .orElse(scala.util.Try(RuntimeMirror.staticModule(qualifierName)))
      .toOption
  }

  private def asClassSymbol(symbol: Symbol): Option[ClassSymbol] = {
    symbol match {
      case aClassSymbol: ClassSymbol => Some(aClassSymbol)
      // Handle the case of a Scala Type which is an alias to a Class
      case aTypeMember: TypeSymbol =>
        val resultType = resultTypeOf(aTypeMember)
        if (resultType.isClass) Some(resultType.asClass) else None
      case _ => None
    }
  }

  private def resultTypeOf(aTypeMember: TypeSymbol) = {
    aTypeMember.typeSignature.resultType.typeSymbol
  }

  private def isNonTrivialClassFullName(name: String) = !isTrivialClassFullName(name)


  private def isTrivialClassFullName(name: String) = TrivialClassFullNames.contains(name)
}
