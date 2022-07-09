package effiban.scala2java.traversers

import effiban.scala2java.transformers.TermInterpolateTransformer
import effiban.scala2java.writers.JavaWriter

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
