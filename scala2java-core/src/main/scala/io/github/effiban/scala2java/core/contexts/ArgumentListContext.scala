package io.github.effiban.scala2java.core.contexts

import io.github.effiban.scala2java.core.entities.ListTraversalOptions

case class ArgumentListContext(options: ListTraversalOptions = ListTraversalOptions(),
                               argNameAsComment: Boolean = false)
