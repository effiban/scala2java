package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.writers.JavaWriter

class Renderers(implicit javaWriter: JavaWriter) {

  val termNameRenderer: TermNameRenderer = new TermNameRendererImpl()

  val typeNameRenderer: TypeNameRenderer = new TypeNameRendererImpl()
}
