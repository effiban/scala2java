package io.github.effiban.scala2java.classifiers

import io.github.effiban.scala2java.entities.TypeNameValues

import scala.meta.Type

trait TypeNameClassifier {

  def isParameterizedType(typeName: Type.Name): Boolean
}

object TypeNameClassifier extends TypeNameClassifier {

  private val ParameterizedTypeNames = Set(
    TypeNameValues.ScalaOption,
    TypeNameValues.Either,
    TypeNameValues.Try,
    TypeNameValues.Future,
    TypeNameValues.Stream,
    TypeNameValues.ScalaArray,
    TypeNameValues.List,
    TypeNameValues.ScalaVector,
    TypeNameValues.Seq,
    TypeNameValues.Set,
    TypeNameValues.Map
  )

  override def isParameterizedType(typeName: Type.Name): Boolean = ParameterizedTypeNames.contains(typeName.value)
}
