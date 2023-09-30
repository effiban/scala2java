package io.github.effiban.scala2java.core.reflection

import scala.util.Try

object JavaReflectionUtils {

  private final val JavaLangPackage = "java.lang"

  def classForName(name: String): Try[Class[_]] = {
    Try(Class.forName(name))
      .orElse(Try(Class.forName(s"$JavaLangPackage.$name")))
  }
}
