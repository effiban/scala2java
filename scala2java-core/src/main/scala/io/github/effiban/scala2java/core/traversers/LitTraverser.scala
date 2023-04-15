package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.LitRenderer

import scala.meta.Lit

trait LitTraverser extends ScalaTreeTraverser[Lit]

class LitTraverserImpl(litRenderer: LitRenderer) extends LitTraverser {

  // Literals in the code
  override def traverse(lit: Lit): Unit = litRenderer.render(lit)
}
