package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emit

import scala.meta.Pat

object PatWildcardTraverser extends ScalaTreeTraverser[Pat.Wildcard] {

  // Wildcard in pattern match expression - translates to Java "default" ?
  def traverse(ignored: Pat.Wildcard): Unit = {
    emit("default")
  }
}
