package io.github.effiban.scala2java.core.reflection

import io.github.effiban.scala2java.core.reflection.ScalaReflectionAccess.RuntimeMirror

import scala.reflect.runtime.universe
import scala.reflect.runtime.universe._

private[reflection] object ScalaReflectionCreator {

  def createTypeTagOf(tpe: universe.Type): TypeTag[Nothing] = {
    TypeTag(RuntimeMirror, new reflect.api.TypeCreator {
      def apply[U <: reflect.api.Universe with Singleton](m: reflect.api.Mirror[U]): U#Type =
        tpe.asInstanceOf[U#Type]
    })
  }

  def createTypeTagOf(tpe: universe.Type, typeArgs: List[universe.Type]): TypeTag[Nothing] = {
    val appliedTypeInstance = appliedType(tpe, typeArgs: _*)
    createTypeTagOf(appliedTypeInstance)
  }
}
