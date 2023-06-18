package io.github.effiban.scala2java.core.traversers

import scala.meta.Init

trait InitTraverser extends ScalaTreeTraverser1[Init]

private[traversers] class InitTraverserImpl(typeTraverser: => TypeTraverser,
                                            expressionTermTraverser: => ExpressionTermTraverser) extends InitTraverser {

  // An 'Init' is an instantiated type, such as with `new` or as a parent in a type definition
  override def traverse(init: Init): Init = {
    val traversedType = typeTraverser.traverse(init.tpe)
    val traversedArgss = init.argss.map(_.map(expressionTermTraverser.traverse))
    init.copy(tpe = traversedType, argss = traversedArgss)
  }
}
