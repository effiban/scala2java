package io.github.effiban.scala2java.core.contexts

import scala.meta.Tree

case class ArgumentContext(maybeParent: Option[Tree] = None, index: Int, argNameAsComment: Boolean = false)
