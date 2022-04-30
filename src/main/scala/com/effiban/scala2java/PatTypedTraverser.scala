package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emit

import scala.meta.Pat

trait PatTypedTraverser extends ScalaTreeTraverser[Pat.Typed]

private[scala2java] class PatTypedTraverserImpl(typeTraverser: => TypeTraverser,
                                                patTraverser: => PatTraverser) extends PatTypedTraverser {

  // Typed pattern expression, e.g. a: Int (in lhs of case clause)
  override def traverse(typedPattern: Pat.Typed): Unit = {
    typeTraverser.traverse(typedPattern.rhs)
    emit(" ")
    patTraverser.traverse(typedPattern.lhs)
  }
}

object PatTypedTraverser extends PatTypedTraverserImpl(TypeTraverser, PatTraverser)
