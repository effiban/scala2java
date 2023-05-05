package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers._
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Pat.{Alternative, Bind}
import scala.meta.{Lit, Pat, Term}

trait PatTraverser extends ScalaTreeTraverser[Pat]

private[traversers] class PatTraverserImpl(litRenderer: LitRenderer,
                                           termNameRenderer: TermNameRenderer,
                                           patWildcardRenderer: PatWildcardRenderer,
                                           patSeqWildcardTraverser: PatSeqWildcardTraverser,
                                           patVarRenderer: PatVarRenderer,
                                           bindTraverser: BindTraverser,
                                           alternativeTraverser: => AlternativeTraverser,
                                           patTupleTraverser: PatTupleTraverser,
                                           patExtractTraverser: PatExtractTraverser,
                                           patExtractInfixTraverser: PatExtractInfixTraverser,
                                           patInterpolateTraverser: PatInterpolateTraverser,
                                           patTypedTraverser: => PatTypedTraverser)
                                          (implicit javaWriter: JavaWriter) extends PatTraverser {

  import javaWriter._

  override def traverse(pat: Pat): Unit = pat match {
    case lit: Lit =>
      litRenderer.render(lit)
    case termName: Term.Name =>
      termNameRenderer.render(termName)
    case patternWildcard: Pat.Wildcard =>
      patWildcardRenderer.render(patternWildcard)
    case patternSeqWildcard: Pat.SeqWildcard =>
      val traversedPatSeqWildcard = patSeqWildcardTraverser.traverse(patternSeqWildcard)
      writeComment(s"UNSUPPORTED: $traversedPatSeqWildcard")
    case patternVar: Pat.Var =>
      patVarRenderer.render(patternVar)
    case patternBind: Bind =>
      val traversedBind = bindTraverser.traverse(patternBind)
      writeComment(s"UNSUPPORTED: $traversedBind")
    case patternAlternative: Alternative => alternativeTraverser.traverse(patternAlternative)
    case patternTuple: Pat.Tuple =>
      val traversedTuple = patTupleTraverser.traverse(patternTuple)
      writeComment(s"UNSUPPORTED: $traversedTuple")
    case patternExtract: Pat.Extract =>
      val traversedPatExtract = patExtractTraverser.traverse(patternExtract)
      writeComment(s"UNSUPPORTED: $traversedPatExtract")
    case patternExtractInfix: Pat.ExtractInfix =>
      val traversedPatExtract = patExtractInfixTraverser.traverse(patternExtractInfix)
      writeComment(s"UNSUPPORTED: $traversedPatExtract")
    case patternInterpolate: Pat.Interpolate =>
      val traversedPatInterpolator = patInterpolateTraverser.traverse(patternInterpolate)
      writeComment(s"UNSUPPORTED: $traversedPatInterpolator")
    case patternTyped: Pat.Typed => patTypedTraverser.traverse(patternTyped)
    case _ => writeComment(s"UNSUPPORTED: $pat")
  }
}
