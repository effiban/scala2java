package com.effiban.scala2java

import com.effiban.scala2java.transformers.TermInterpolateTransformer

import scala.meta.Term

trait TermInterpolateTraverser extends ScalaTreeTraverser[Term.Interpolate]

private[scala2java] class TermInterpolateTraverserImpl(termInterpolateTransformer: TermInterpolateTransformer,
                                                       termApplyTraverser: => TermApplyTraverser)
                                                      (implicit javaEmitter: JavaEmitter) extends TermInterpolateTraverser {
  import javaEmitter._

  override def traverse(termInterpolate: Term.Interpolate): Unit = {
    termInterpolateTransformer.transform(termInterpolate) match {
      case Some(termApply) => termApplyTraverser.traverse(termApply)
      case _ => emitComment(s"UNSUPPORTED interpolation: $termInterpolate")
    }
  }
}

object TermInterpolateTraverser extends TermInterpolateTraverserImpl(
  TermInterpolateTransformer,
  TermApplyTraverser
)
