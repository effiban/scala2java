package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emitComment

import scala.meta.Term.{Apply, Select}
import scala.meta.{Lit, Term}

trait TermInterpolateTraverser extends ScalaTreeTraverser[Term.Interpolate]

private[scala2java] class TermInterpolateTraverserImpl(termApplyTraverser: => TermApplyTraverser)
                                                      (implicit javaEmitter: JavaEmitter) extends TermInterpolateTraverser {

  override def traverse(termInterpolate: Term.Interpolate): Unit = {
    // Transform Scala string interpolation to Java String.format()
    termInterpolate.prefix match {
      case Term.Name("s") => termApplyTraverser.traverse(toJavaStringFormatInvocation(termInterpolate.parts, termInterpolate.args))
      case _ => emitComment(s"UNSUPPORTED interpolation: $termInterpolate")
    }
  }

  private def toJavaStringFormatInvocation(formatParts: List[Lit], interpolationArgs: List[Term]) = {
    Apply(Select(Term.Name("String"), Term.Name("format")), List(Lit.String(formatParts.mkString("%s"))) ++ interpolationArgs)
  }
}

object TermInterpolateTraverser extends TermInterpolateTraverserImpl(TermApplyTraverser)
