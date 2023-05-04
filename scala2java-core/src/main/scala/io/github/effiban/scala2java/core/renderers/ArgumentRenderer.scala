package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.contexts.ArgumentContext

import scala.meta.Tree

trait ArgumentRenderer[T <: Tree] {

  def render(arg: T, context: ArgumentContext): Unit
}
