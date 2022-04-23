package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emit

import scala.meta.Name

object NameIndeterminateTraverser extends ScalaTreeTraverser[Name.Indeterminate] {

  // Name that cannot be distinguished between a term and a type (for example, name in an "import" clause)
  // Since it cannot be distinguished, we can assume that the name should be unchanged from Scala (there is no rule to convert by)
  def traverse(indeterminateName: Name.Indeterminate): Unit = {
    emit(indeterminateName.value)
  }
}
