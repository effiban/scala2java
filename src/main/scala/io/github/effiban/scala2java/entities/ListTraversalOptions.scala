package io.github.effiban.scala2java.entities

import io.github.effiban.scala2java.entities.EnclosingDelimiter.EnclosingDelimiter

case class ListTraversalOptions(onSameLine: Boolean = false,
                                maybeEnclosingDelimiter: Option[EnclosingDelimiter] = None,
                                traverseEmpty: Boolean = false)
