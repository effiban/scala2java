package io.github.effiban.scala2java.core.traversers

import scala.meta.Self

trait SelfTraverser extends ScalaTreeTraverser1[Self]

private[traversers] class SelfTraverserImpl(typeTraverser: => TypeTraverser) extends SelfTraverser {

  override def traverse(`self`: Self): Self = {
    //TODO - consider translating the 'self' type into a Java parent
    `self`.copy(decltpe = `self`.decltpe.map(typeTraverser.traverse))
  }
}
