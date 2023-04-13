package io.github.effiban.scala2java.core.classifiers

import scala.meta.Type

trait TypeClassifier[T <: Type] {

  def isJavaStreamLike(tpe: T): Boolean

  def isJavaListLike(tpe: T): Boolean

  def isJavaSetLike(tpe: T): Boolean

  def isJavaMapLike(tpe: T): Boolean
}
