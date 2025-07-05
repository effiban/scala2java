package io.github.effiban.scala2java.core.renderers

import scala.meta.Type

trait TypeSingletonRenderer extends JavaTreeRenderer[Type.Singleton]

private[renderers] class TypeSingletonRendererImpl(termRefRenderer: TermRefRenderer) extends TypeSingletonRenderer {

  override def render(singletonType: Type.Singleton): Unit = {
    termRefRenderer.render(singletonType.ref)
  }
}
