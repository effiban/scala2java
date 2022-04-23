package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emit

import scala.meta.Pat
import scala.meta.Pat.Bind

object BindTraverser extends ScalaTreeTraverser[Pat.Bind] {

  // Pattern match bind variable, e.g.: a @ A()
  override def traverse(patternBind: Bind): Unit = {
    // In Java (when supported) the order is reversed
    PatTraverser.traverse(patternBind.rhs)
    emit(" ")
    PatTraverser.traverse(patternBind.lhs)
  }
}
