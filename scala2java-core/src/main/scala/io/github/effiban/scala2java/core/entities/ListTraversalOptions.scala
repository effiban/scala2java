package io.github.effiban.scala2java.core.entities

import io.github.effiban.scala2java.core.entities.EnclosingDelimiter.EnclosingDelimiter

// TODO rename to ListRendererOptions once the traversers are no longer using it
case class ListTraversalOptions(onSameLine: Boolean = false,
                                maybeEnclosingDelimiter: Option[EnclosingDelimiter] = None,
                                traverseEmpty: Boolean = false)
