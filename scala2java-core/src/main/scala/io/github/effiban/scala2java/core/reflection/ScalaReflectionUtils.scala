package io.github.effiban.scala2java.core.reflection

import io.github.effiban.scala2java.core.entities.ReflectedEntities.RuntimeMirror

import scala.annotation.tailrec
import scala.meta.{Defn, Member, Pkg, Term, Type}
import scala.reflect.runtime.universe._

object ScalaReflectionUtils {

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

  def baseClassesOf(cls: ClassSymbol): List[ClassSymbol] = {
    cls.baseClasses.flatMap(asClassSymbol)
  }

  def isTermMemberOf(symbol: Symbol, termName: Term.Name): Boolean = {
    symbol.info.member(TermName(termName.value)) match {
      case NoSymbol => false
      case _ => true
    }
  }

  def isTypeMemberOf(symbol: Symbol, typeName: Type.Name): Boolean = {
    symbol.typeSignature.decl(TypeName(typeName.value)) match {
      case NoSymbol => false
      case _ => true
    }
  }

  @tailrec
  private def symbolOf(symbol: Symbol, memberPath: List[Member]): Option[Symbol] = {
    (symbol, memberPath) match {
      case (symbol: Symbol, (classOrTrait@(_ : Defn.Class | _: Defn.Trait)) :: members) =>
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
}
