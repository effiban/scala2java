package com.effiban.scala2java

import scala.meta.Pat

trait PatWildcardTraverser extends ScalaTreeTraverser[Pat.Wildcard]

private[scala2java] class PatWildcardTraverserImpl(javaEmitter: JavaEmitter) extends PatWildcardTraverser {

  import javaEmitter._

  // Wildcard in pattern match expression - translates to Java "default" ?
  override def traverse(ignored: Pat.Wildcard): Unit = {
    emit("default")
  }
}

object PatWildcardTraverser extends PatWildcardTraverserImpl(JavaEmitter)
