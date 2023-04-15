package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.PatExtractRenderer

import scala.meta.Pat

trait PatExtractTraverser extends ScalaTreeTraverser[Pat.Extract]

class PatExtractTraverserImpl(patExtractRenderer: PatExtractRenderer) extends PatExtractTraverser {

  override def traverse(patternExtractor: Pat.Extract): Unit = {
    //TODO - unsupported in Java, but consider transforming it to a guard
    patExtractRenderer.render(patternExtractor)
  }
}
