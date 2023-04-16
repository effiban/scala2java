package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.TypeAnonymousParamRenderer

import scala.meta.Type

trait TypeAnonymousParamTraverser extends ScalaTreeTraverser[Type.AnonymousParam]

private[traversers] class TypeAnonymousParamTraverserImpl(typeAnonymousParamRenderer: TypeAnonymousParamRenderer)
  extends TypeAnonymousParamTraverser {

  // Underscore in type param with variance modifier e.g. T[+_] (not sure about this, anyway ignoring the variance)
  override def traverse(anonymousParamType: Type.AnonymousParam): Unit = {
    typeAnonymousParamRenderer.render(anonymousParamType)
  }
}
