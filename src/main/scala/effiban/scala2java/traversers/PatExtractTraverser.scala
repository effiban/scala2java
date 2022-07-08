package effiban.scala2java.traversers

import effiban.scala2java.writers.JavaWriter

import scala.meta.Pat

trait PatExtractTraverser extends ScalaTreeTraverser[Pat.Extract]

class PatExtractTraverserImpl(implicit javaWriter: JavaWriter) extends PatExtractTraverser {

  import javaWriter._

  /**
   * Pattern match extractor e.g. {{{MyRecord(a, b)}}}
   */
  override def traverse(patternExtractor: Pat.Extract): Unit = {
    //TODO - unsupported in Java, but consider transforming it to a guard
    writeComment(s"${patternExtractor.fun}(${patternExtractor.args.mkString(", ")})")
  }
}

object PatExtractTraverser extends PatExtractTraverserImpl()
