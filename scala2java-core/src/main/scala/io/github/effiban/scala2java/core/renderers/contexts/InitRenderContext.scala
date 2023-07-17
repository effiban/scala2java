package io.github.effiban.scala2java.core.renderers.contexts

case class InitRenderContext(ignoreArgs: Boolean = false,
                             renderEmpty: Boolean = false,
                             argNameAsComment: Boolean = false)
