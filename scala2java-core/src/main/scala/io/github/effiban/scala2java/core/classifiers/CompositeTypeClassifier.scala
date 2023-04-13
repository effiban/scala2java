package io.github.effiban.scala2java.core.classifiers

import scala.meta.Type

trait CompositeTypeClassifier extends TypeClassifier[Type]

private[classifiers] class CompositeTypeClassifierImpl(typeNameClassifier: TypeNameClassifier) extends CompositeTypeClassifier {

  override def isJavaStreamLike(tpe: Type): Boolean = typeNameMatches(tpe, typeNameClassifier.isJavaStreamLike)

  override def isJavaListLike(tpe: Type): Boolean = typeNameMatches(tpe, typeNameClassifier.isJavaListLike)

  override def isJavaSetLike(tpe: Type): Boolean = typeNameMatches(tpe, typeNameClassifier.isJavaSetLike)

  override def isJavaMapLike(tpe: Type): Boolean = typeNameMatches(tpe, typeNameClassifier.isJavaMapLike)

  private def typeNameMatches(tpe: Type, typeNamePredicate: Type.Name => Boolean): Boolean = tpe match {
    case typeName: Type.Name => typeNamePredicate(typeName)
    case Type.Apply(typeName: Type.Name, _) => typeNamePredicate(typeName)
    case _ => false
  }
}

object CompositeTypeClassifier extends CompositeTypeClassifierImpl(TypeNameClassifier)
