package io.github.effiban.scala2java.core.traversers

import scala.meta.Pat

trait PatExtractTraverser extends ScalaTreeTraverser1[Pat.Extract]

object PatExtractTraverser extends PatExtractTraverser {

  /** Pattern extractor, e.g. {{{MyObj(1, 2)}}} in {{{case MyObj(1, 2) => ....}}} */
  //TODO - unsupported in Java, but consider transforming it to a guard
  override def traverse(patternExtractor: Pat.Extract): Pat.Extract = {
    patternExtractor
  }
}
