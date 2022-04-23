package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.{emitArrow, emitParametersEnd, emitParametersStart}
import com.effiban.scala2java.TraversalContext.javaOwnerContext

import scala.meta.Term

object TermFunctionTraverser extends ScalaTreeTraverser[Term.Function] {

  // lambda definition
  override def traverse(function: Term.Function): Unit = {
    val outerJavaOwnerContext = javaOwnerContext
    javaOwnerContext = Lambda
    function.params match {
      case Nil =>
      case param :: Nil => GenericTreeTraverser.traverse(param)
      case _ =>
        emitParametersStart()
        GenericTreeTraverser.traverse(function.params)
        emitParametersEnd()
    }
    emitArrow()
    GenericTreeTraverser.traverse(function.body)
    javaOwnerContext = outerJavaOwnerContext
  }
}
