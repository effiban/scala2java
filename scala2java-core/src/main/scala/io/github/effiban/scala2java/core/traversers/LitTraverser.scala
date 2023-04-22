package io.github.effiban.scala2java.core.traversers

import scala.meta.Lit

trait LitTraverser extends ScalaTreeTraverser1[Lit]

object LitTraverser extends LitTraverser {

  // Literals in the code
  override def traverse(lit: Lit): Lit = lit
}
