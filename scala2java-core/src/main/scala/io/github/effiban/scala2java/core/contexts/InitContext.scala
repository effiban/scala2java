package io.github.effiban.scala2java.core.contexts

case class InitContext(ignoreArgs: Boolean = false,
                       traverseEmpty: Boolean = false,
                       argNameAsComment: Boolean = false)
