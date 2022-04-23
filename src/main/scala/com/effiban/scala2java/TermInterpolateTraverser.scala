package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emitComment

import scala.meta.Term.{Apply, Select}
import scala.meta.{Lit, Term}

object TermInterpolateTraverser extends ScalaTreeTraverser[Term.Interpolate] {

  def traverse(termInterpolate: Term.Interpolate): Unit = {
    // Transform Scala string interpolation to Java String.format()
    termInterpolate.prefix match {
      case Term.Name("s") => GenericTreeTraverser.traverse(toJavaStringFormatInvocation(termInterpolate.parts, termInterpolate.args))
      case _ => emitComment(s"UNRECOGNIZED interpolation: $termInterpolate")
    }
  }

  private def toJavaStringFormatInvocation(formatParts: List[Lit], interpolationArgs: List[Term]) = {
    Apply(Select(Term.Name("String"), Term.Name("format")), List(Lit.String(formatParts.mkString("%s"))) ++ interpolationArgs)
  }
}
