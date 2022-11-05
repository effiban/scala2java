package io.github.effiban.scala2java.core.entities

import io.github.effiban.scala2java.core.entities.EnclosingDelimiter.EnclosingDelimiter

case class ListTraversalOptions(onSameLine: Boolean = false,
                                maybeEnclosingDelimiter: Option[EnclosingDelimiter] = None,
                                traverseEmpty: Boolean = false)
