package io.github.effiban.scala2java.core.traversers

import scala.meta.Pat
import scala.meta.Pat.{Alternative, Bind}

trait PatTraverser extends ScalaTreeTraverser1[Pat]

private[traversers] class PatTraverserImpl(patSeqWildcardTraverser: PatSeqWildcardTraverser,
                                           bindTraverser: BindTraverser,
                                           alternativeTraverser: => AlternativeTraverser,
                                           patTupleTraverser: PatTupleTraverser,
                                           patExtractTraverser: PatExtractTraverser,
                                           patExtractInfixTraverser: PatExtractInfixTraverser,
                                           patInterpolateTraverser: PatInterpolateTraverser,
                                           patTypedTraverser: => PatTypedTraverser) extends PatTraverser {

  override def traverse(pat: Pat): Pat = pat match {
    case patternSeqWildcard: Pat.SeqWildcard => patSeqWildcardTraverser.traverse(patternSeqWildcard)
    case patternBind: Bind => bindTraverser.traverse(patternBind)
    case patternAlternative: Alternative => alternativeTraverser.traverse(patternAlternative)
    case patternTuple: Pat.Tuple => patTupleTraverser.traverse(patternTuple)
    case patternExtract: Pat.Extract => patExtractTraverser.traverse(patternExtract)
    case patternExtractInfix: Pat.ExtractInfix => patExtractInfixTraverser.traverse(patternExtractInfix)
    case patternInterpolate: Pat.Interpolate => patInterpolateTraverser.traverse(patternInterpolate)
    case patternTyped: Pat.Typed => patTypedTraverser.traverse(patternTyped)
    case other => other
  }
}
