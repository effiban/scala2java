package io.github.effiban.scala2java.traversers

import io.github.effiban.scala2java.writers.JavaWriter

import scala.meta.Type

trait TypeAnonymousParamTraverser extends ScalaTreeTraverser[Type.AnonymousParam]

private[traversers] class TypeAnonymousParamTraverserImpl(implicit javaWriter: JavaWriter) extends TypeAnonymousParamTraverser {

  import javaWriter._

  // Underscore in type param with variance modifier e.g. T[+_] (not sure about this, anyway ignoring the variance)
  override def traverse(anonymousParamType: Type.AnonymousParam): Unit = {
    write("?")
  }
}
