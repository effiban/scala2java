package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emit

import scala.meta.Pat
import scala.meta.Pat.Bind

trait BindTraverser extends ScalaTreeTraverser[Pat.Bind]

private[scala2java] class BindTraverserImpl(patTraverser: => PatTraverser) extends BindTraverser {

  // Pattern match bind variable, e.g.: a @ A()
  override def traverse(patternBind: Bind): Unit = {
    // In Java (when supported) the order is reversed
    patTraverser.traverse(patternBind.rhs)
    emit(" ")
    patTraverser.traverse(patternBind.lhs)
  }
}

object BindTraverser extends BindTraverserImpl(PatTraverser)
