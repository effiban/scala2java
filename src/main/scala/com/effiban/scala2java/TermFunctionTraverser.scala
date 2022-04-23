package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.{Parentheses, emitArrow}
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
      case _ => ArgumentListTraverser.traverse(function.params, maybeDelimiterType = Some(Parentheses))
    }
    emitArrow()
    GenericTreeTraverser.traverse(function.body)
    javaOwnerContext = outerJavaOwnerContext
  }
}
