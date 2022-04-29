package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emitArrow
import com.effiban.scala2java.TraversalContext.javaOwnerContext

import scala.meta.Term

trait TermFunctionTraverser extends ScalaTreeTraverser[Term.Function]

object TermFunctionTraverser extends TermFunctionTraverser {

  // lambda definition
  override def traverse(function: Term.Function): Unit = {
    val outerJavaOwnerContext = javaOwnerContext
    javaOwnerContext = Lambda
    function.params match {
      case Nil =>
      case param :: Nil => TermParamTraverser.traverse(param)
      case _ => TermParamListTraverser.traverse(function.params)
    }
    emitArrow()
    TermTraverser.traverse(function.body)
    javaOwnerContext = outerJavaOwnerContext
  }
}
