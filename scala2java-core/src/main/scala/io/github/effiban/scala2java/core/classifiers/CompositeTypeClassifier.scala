package io.github.effiban.scala2java.core.classifiers

import scala.meta.Type

trait CompositeTypeClassifier extends TypeClassifier[Type]

private[classifiers] class CompositeTypeClassifierImpl(typeRefClassifier: TypeRefClassifier) extends CompositeTypeClassifier {

  override def isJavaStreamLike(tpe: Type): Boolean = typeRefMatches(tpe, typeRefClassifier.isJavaStreamLike)

  override def isJavaListLike(tpe: Type): Boolean = typeRefMatches(tpe, typeRefClassifier.isJavaListLike)

  override def isJavaSetLike(tpe: Type): Boolean = typeRefMatches(tpe, typeRefClassifier.isJavaSetLike)

  override def isJavaMapLike(tpe: Type): Boolean = typeRefMatches(tpe, typeRefClassifier.isJavaMapLike)

  private def typeRefMatches(tpe: Type, typeRefPredicate: Type.Ref => Boolean): Boolean = tpe match {
    case typeRef: Type.Ref => typeRefPredicate(typeRef)
    case Type.Apply(typeRef: Type.Ref, _) => typeRefPredicate(typeRef)
    case _ => false
  }
}

object CompositeTypeClassifier extends CompositeTypeClassifierImpl(TypeRefClassifier)
