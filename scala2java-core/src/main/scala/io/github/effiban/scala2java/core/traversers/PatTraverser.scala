package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers._
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Pat.{Alternative, Bind}
import scala.meta.{Lit, Pat, Term}

trait PatTraverser extends ScalaTreeTraverser[Pat]

private[traversers] class PatTraverserImpl(litTraverser: LitTraverser,
                                           litRenderer: LitRenderer,
                                           defaultTermNameTraverser: => TermNameTraverser,
                                           patWildcardTraverser: PatWildcardTraverser,
                                           patWildcardRenderer: PatWildcardRenderer,
                                           patSeqWildcardTraverser: PatSeqWildcardTraverser,
                                           patSeqWildcardRenderer: PatSeqWildcardRenderer,
                                           patVarRenderer: PatVarRenderer,
                                           bindTraverser: BindTraverser,
                                           bindRenderer: BindRenderer,
                                           alternativeTraverser: => AlternativeTraverser,
                                           patTupleTraverser: PatTupleTraverser,
                                           patTupleRenderer: PatTupleRenderer,
                                           patExtractTraverser: PatExtractTraverser,
                                           patExtractRenderer: PatExtractRenderer,
                                           patExtractInfixTraverser: PatExtractInfixTraverser,
                                           patInterpolateTraverser: PatInterpolateTraverser,
                                           patInterpolateRenderer: PatInterpolateRenderer,
                                           patTypedTraverser: => PatTypedTraverser)
                                          (implicit javaWriter: JavaWriter) extends PatTraverser {

  import javaWriter._

  override def traverse(pat: Pat): Unit = pat match {
    case lit: Lit =>
      val traversedLit = litTraverser.traverse(lit)
      litRenderer.render(traversedLit)
    case termName: Term.Name => defaultTermNameTraverser.traverse(termName)
    case patternWildcard: Pat.Wildcard =>
      val traversedPatWildcard = patWildcardTraverser.traverse(patternWildcard)
      patWildcardRenderer.render(traversedPatWildcard)
    case patternSeqWildcard: Pat.SeqWildcard =>
      val traversedPatSeqWildcard = patSeqWildcardTraverser.traverse(patternSeqWildcard)
      patSeqWildcardRenderer.render(traversedPatSeqWildcard)
    case patternVar: Pat.Var =>
      patVarRenderer.render(patternVar)
    case patternBind: Bind =>
      val traversedBind = bindTraverser.traverse(patternBind)
      bindRenderer.render(traversedBind)
    case patternAlternative: Alternative => alternativeTraverser.traverse(patternAlternative)
    case patternTuple: Pat.Tuple =>
      val traversedTuple = patTupleTraverser.traverse(patternTuple)
      patTupleRenderer.render(traversedTuple)
    case patternExtract: Pat.Extract =>
      val traversedPatExtract = patExtractTraverser.traverse(patternExtract)
      patExtractRenderer.render(traversedPatExtract)
    case patternExtractInfix: Pat.ExtractInfix =>
      val traversedPatExtract = patExtractInfixTraverser.traverse(patternExtractInfix)
      patExtractRenderer.render(traversedPatExtract)
    case patternInterpolate: Pat.Interpolate =>
      val traversedPatInterpolator = patInterpolateTraverser.traverse(patternInterpolate)
      patInterpolateRenderer.render(traversedPatInterpolator)
    case patternTyped: Pat.Typed => patTypedTraverser.traverse(patternTyped)
    case _ => writeComment(s"UNSUPPORTED: $pat")
  }
}
