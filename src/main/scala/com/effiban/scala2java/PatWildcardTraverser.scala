package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emit

import scala.meta.Pat

trait PatWildcardTraverser extends ScalaTreeTraverser[Pat.Wildcard]

object PatWildcardTraverser extends PatWildcardTraverser {

  // Wildcard in pattern match expression - translates to Java "default" ?
  override def traverse(ignored: Pat.Wildcard): Unit = {
    emit("default")
  }
}
