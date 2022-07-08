package effiban.scala2java.traversers

import effiban.scala2java.writers.JavaWriter

import scala.meta.Pat

trait PatInterpolateTraverser extends ScalaTreeTraverser[Pat.Interpolate]

private[scala2java] class PatInterpolateTraverserImpl(implicit javaWriter: JavaWriter) extends PatInterpolateTraverser {

  import javaWriter._

  // Pattern interpolation e.g. r"Hello (.+)$name"
  override def traverse(patternInterpolation: Pat.Interpolate): Unit = {
    //TODO consider rewriting with Java Pattern and Matcher
    writeComment(patternInterpolation.toString())
  }
}

object PatInterpolateTraverser extends PatInterpolateTraverserImpl()
