package com.effiban.scala2java

import com.effiban.scala2java.TraversalConstants.JavaPlaceholder

import scala.meta.Term
import scala.meta.Term.{AnonymousFunction, Param}

object AnonymousFunctionTraverser extends ScalaTreeTraverser[AnonymousFunction] {

  override def traverse(anonymousFunction: AnonymousFunction): Unit = {
    GenericTreeTraverser.traverse(Term.Function(
      params = List(Param(name = Term.Name(JavaPlaceholder), mods = Nil, decltpe = None, default = None)),
      body = anonymousFunction.body))
  }
}
