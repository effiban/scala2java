package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.SelfRenderer

import scala.meta.Self

trait SelfTraverser extends ScalaTreeTraverser[Self]

private[traversers] class SelfTraverserImpl(selfRenderer: SelfRenderer) extends SelfTraverser {

  override def traverse(`self`: Self): Unit = {
    //TODO - consider translating the 'self' type into a Java parent
    selfRenderer.render(`self`)
  }
}
