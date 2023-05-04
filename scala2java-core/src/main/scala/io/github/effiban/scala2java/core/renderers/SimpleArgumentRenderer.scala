package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.contexts.ArgumentContext

import scala.meta.Tree

class SimpleArgumentRenderer[T <: Tree](renderer: JavaTreeRenderer[T]) extends ArgumentRenderer[T] {
  override def render(arg: T, context: ArgumentContext): Unit = renderer.render(arg)
}
