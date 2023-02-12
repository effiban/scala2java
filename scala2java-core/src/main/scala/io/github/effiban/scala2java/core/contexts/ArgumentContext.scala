package io.github.effiban.scala2java.core.contexts

import scala.meta.{Term, Tree}

case class ArgumentContext(maybeParent: Option[Tree] = None,
                           maybeName: Option[Term.Name] = None,
                           index: Int,
                           argNameAsComment: Boolean = false)

