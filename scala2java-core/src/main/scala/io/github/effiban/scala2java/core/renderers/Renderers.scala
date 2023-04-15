package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.writers.JavaWriter

class Renderers(implicit javaWriter: JavaWriter) {

  val bindRenderer: BindRenderer = new BindRendererImpl()

  val litRenderer: LitRenderer = new LitRendererImpl()

  val nameIndeterminateRenderer: NameIndeterminateRenderer = new NameIndeterminateRendererImpl()

  val patExtractRenderer: PatExtractRenderer = new PatExtractRendererImpl()

  val patInterpolateRenderer: PatInterpolateRenderer = new PatInterpolateRendererImpl()

  val termNameRenderer: TermNameRenderer = new TermNameRendererImpl()

  val typeNameRenderer: TypeNameRenderer = new TypeNameRendererImpl()
}
