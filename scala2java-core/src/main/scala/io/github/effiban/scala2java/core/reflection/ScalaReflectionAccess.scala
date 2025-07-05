package io.github.effiban.scala2java.core.reflection

import scala.reflect.runtime.universe._

private[reflection] object ScalaReflectionAccess {

  final val RuntimeMirror = runtimeMirror(getClass.getClassLoader)
}
