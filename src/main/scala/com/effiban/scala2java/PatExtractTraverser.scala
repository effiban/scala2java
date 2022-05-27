package com.effiban.scala2java

import scala.meta.Pat

trait PatExtractTraverser extends ScalaTreeTraverser[Pat.Extract]

class PatExtractTraverserImpl(implicit javaEmitter: JavaEmitter) extends PatExtractTraverser {
  import javaEmitter._

  /**
   * Pattern match extractor e.g. {{{MyRecord(a, b)}}}
   */
  override def traverse(patternExtractor: Pat.Extract): Unit = {
    //TODO - unsupported in Java, but consider transforming it to a guard
    emitComment(s"${patternExtractor.fun}(${patternExtractor.args.mkString(", ")})")
  }
}

object PatExtractTraverser extends PatExtractTraverserImpl()
