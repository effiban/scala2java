package io.github.effiban.scala2java.core.renderers.contexts

// TODO - when expecting a return value which is a lambda, need another flag for returnability inside the lambda body
case class BlockStatRenderContext(uncertainReturn: Boolean = false)
