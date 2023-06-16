package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{ArgumentListContext, InitContext}
import io.github.effiban.scala2java.core.entities.EnclosingDelimiter.Parentheses
import io.github.effiban.scala2java.core.entities.ListTraversalOptions
import io.github.effiban.scala2java.core.renderers.TypeRenderer

import scala.meta.{Init, Term}

@deprecated
trait DeprecatedInitTraverser {
  def traverse(init: Init, context: InitContext = InitContext()): Unit
}

@deprecated
private[traversers] class DeprecatedInitTraverserImpl(typeTraverser: => TypeTraverser,
                                                      typeRenderer: => TypeRenderer,
                                                      argumentListTraverser: => DeprecatedArgumentListTraverser,
                                                      invocationArgTraverser: => DeprecatedArgumentTraverser[Term]) extends DeprecatedInitTraverser {

  // An 'Init' is an instantiated type, such as with `new` or as a parent in a type definition
  override def traverse(init: Init, context: InitContext = InitContext()): Unit = {
    val traversedType = typeTraverser.traverse(init.tpe)
    typeRenderer.render(traversedType)

    if (!context.ignoreArgs) {
      val options = ListTraversalOptions(traverseEmpty = context.traverseEmpty, maybeEnclosingDelimiter = Some(Parentheses))
      val argListContext = ArgumentListContext(options = options, argNameAsComment = context.argNameAsComment)
      argumentListTraverser.traverse(
        args = init.argss.flatten,
        argTraverser = invocationArgTraverser,
        context = argListContext)
    }
  }
}
