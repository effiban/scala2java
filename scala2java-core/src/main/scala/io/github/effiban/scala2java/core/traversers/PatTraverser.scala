package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.{PatExtractRenderer, PatInterpolateRenderer, PatSeqWildcardRenderer}
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Pat.{Alternative, Bind}
import scala.meta.{Lit, Pat, Term}

trait PatTraverser extends ScalaTreeTraverser[Pat]

private[traversers] class PatTraverserImpl(litTraverser: => LitTraverser,
                                           defaultTermNameTraverser: => TermNameTraverser,
                                           patWildcardTraverser: => PatWildcardTraverser,
                                           patSeqWildcardTraverser: PatSeqWildcardTraverser,
                                           patSeqWildcardRenderer: PatSeqWildcardRenderer,
                                           patVarTraverser: => PatVarTraverser,
                                           bindTraverser: => BindTraverser,
                                           alternativeTraverser: => AlternativeTraverser,
                                           patTupleTraverser: => PatTupleTraverser,
                                           patExtractTraverser: PatExtractTraverser,
                                           patExtractRenderer: PatExtractRenderer,
                                           patExtractInfixTraverser: PatExtractInfixTraverser,
                                           patInterpolateTraverser: PatInterpolateTraverser,
                                           patInterpolateRenderer: PatInterpolateRenderer,
                                           patTypedTraverser: => PatTypedTraverser)
                                          (implicit javaWriter: JavaWriter) extends PatTraverser {

  import javaWriter._

  override def traverse(pat: Pat): Unit = pat match {
    case lit: Lit => litTraverser.traverse(lit)
    case termName: Term.Name => defaultTermNameTraverser.traverse(termName)
    case patternWildcard: Pat.Wildcard => patWildcardTraverser.traverse(patternWildcard)
    case patternSeqWildcard: Pat.SeqWildcard =>
      val traversedPatSeqWildcard = patSeqWildcardTraverser.traverse(patternSeqWildcard)
      patSeqWildcardRenderer.render(traversedPatSeqWildcard)
    case patternVar: Pat.Var => patVarTraverser.traverse(patternVar)
    case patternBind: Bind => bindTraverser.traverse(patternBind)
    case patternAlternative: Alternative => alternativeTraverser.traverse(patternAlternative)
    case patternTuple: Pat.Tuple => patTupleTraverser.traverse(patternTuple)
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
