package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.transformers.TermInterpolateTransformer
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Term

trait TermInterpolateTraverser extends ScalaTreeTraverser[Term.Interpolate]

private[traversers] class TermInterpolateTraverserImpl(termInterpolateTransformer: TermInterpolateTransformer,
                                                       termApplyTraverser: => TermApplyTraverser)
                                                      (implicit javaWriter: JavaWriter) extends TermInterpolateTraverser {

  import javaWriter._

  override def traverse(termInterpolate: Term.Interpolate): Unit = {
    termInterpolateTransformer.transform(termInterpolate) match {
      case Some(termApply) => termApplyTraverser.traverse(termApply)
      case _ => writeComment(s"UNSUPPORTED interpolation: $termInterpolate")
    }
  }
}
