package io.github.effiban.scala2java.core.entities

import scala.reflect.runtime.universe._
object ReflectedEntities {

  final val RuntimeMirror = runtimeMirror(getClass.getClassLoader)

  final val PredefModule = RuntimeMirror.staticModule("scala.Predef")
  final val ScalaPackage = RuntimeMirror.staticPackage("scala")
}
