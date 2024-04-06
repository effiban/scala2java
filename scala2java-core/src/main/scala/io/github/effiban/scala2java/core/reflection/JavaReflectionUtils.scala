package io.github.effiban.scala2java.core.reflection

import java.lang.reflect.Modifier.isStatic
import java.lang.reflect.{Field, Method}
import scala.util.{Success, Try}

object JavaReflectionUtils {

  private final val JavaLangPackage = "java.lang"

  def classForName(name: String): Option[Class[_]] = {
    Try(Class.forName(name))
      .orElse(Try(Class.forName(s"$JavaLangPackage.$name")))
      .toOption
  }

  def staticFieldFor(cls: Class[_], name: String): Option[Field] = {
      Try(cls.getField(name)) match {
        case Success(field) if isStatic(field.getModifiers) => Some(field)
        case _ => None
      }
  }

  def staticMethodFor(cls: Class[_], name: String, numArgs: Int): Option[Method] = {
    cls.getMethods
      .filter(_.getName == name)
      .filter(method => isStatic(method.getModifiers))
      .collectFirst { case method if method.getParameterCount == numArgs => method }
  }
}
