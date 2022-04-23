package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emit
import com.effiban.scala2java.TraversalConstants.JavaPlaceholder

import scala.meta.Term

object TermPlaceholderTraverser extends ScalaTreeTraverser[Term.Placeholder] {

  // Underscore as expression - will compile in java only if it is an anonymous function, but rendering always
  def traverse(ignored: Term.Placeholder): Unit = {
    emit(JavaPlaceholder)
  }
}
