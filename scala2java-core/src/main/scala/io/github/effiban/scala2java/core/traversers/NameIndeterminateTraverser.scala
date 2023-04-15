package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.NameIndeterminateRenderer

import scala.meta.Name

trait NameIndeterminateTraverser extends ScalaTreeTraverser[Name.Indeterminate]

class NameIndeterminateTraverserImpl(nameIndeterminateRenderer: NameIndeterminateRenderer) extends NameIndeterminateTraverser {

  // Name that cannot be distinguished between a term and a type (for example, name in an "import" clause)
  // Since it cannot be distinguished, we can assume that the name should be unchanged from Scala (there is no rule to convert by)
  override def traverse(indeterminateName: Name.Indeterminate): Unit = {
    nameIndeterminateRenderer.render(indeterminateName)
  }
}
