package io.github.effiban.scala2java.core.reflection

import io.github.effiban.scala2java.core.reflection.ScalaReflectionTransformer.classSymbolOf

import scala.meta.Type
import scala.reflect.runtime.universe._

trait ScalaReflectionClassifier {
  def isNonTrivialEmptyType(typeRef: Type.Ref): Boolean
}

object ScalaReflectionClassifier extends ScalaReflectionClassifier {

  private val TrivialClassFullNames =
    Set(
      "java.lang.Object",
      "java.io.Serializable",
      "scala.Any",
      "scala.AnyRef",
      "scala.AnyVal",
      "scala.Product"
    )

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

  private[reflection] def isNonTrivialClassFullName(name: String) = !isTrivialClassFullName(name)

  private[reflection] def isTrivialClassFullName(name: String) = TrivialClassFullNames.contains(name)
}
