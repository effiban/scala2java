package io.github.effiban.scala2java.core.contexts

import io.github.effiban.scala2java.core.entities.ListTraversalOptions

import scala.meta.Tree

case class ArgumentListContext(maybeParent: Option[Tree] = None,
                               options: ListTraversalOptions = ListTraversalOptions(),
                               argNameAsComment: Boolean = false)
