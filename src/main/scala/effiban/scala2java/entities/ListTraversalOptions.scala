package effiban.scala2java.entities

import effiban.scala2java.entities.EnclosingDelimiter.EnclosingDelimiter

case class ListTraversalOptions(onSameLine: Boolean = false,
                                maybeEnclosingDelimiter: Option[EnclosingDelimiter] = None,
                                traverseEmpty: Boolean = false)
