package io.github.effiban.scala2java.core.entities

import scala.reflect.runtime.universe._

object ScalaReflectedEntities {

  final val RuntimeMirror = runtimeMirror(getClass.getClassLoader)
}
