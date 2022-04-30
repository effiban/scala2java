package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emitComment

import scala.meta.Pat.{Alternative, Bind}
import scala.meta.{Lit, Pat, Term}

trait PatTraverser extends ScalaTreeTraverser[Pat]

private[scala2java] class PatTraverserImpl(termNameTraverser: => TermNameTraverser,
                                           patWildcardTraverser: => PatWildcardTraverser,
                                           patSeqWildcardTraverser: => PatSeqWildcardTraverser,
                                           patVarTraverser: => PatVarTraverser,
                                           bindTraverser: => BindTraverser,
                                           alternativeTraverser: => AlternativeTraverser,
                                           patTupleTraverser: => PatTupleTraverser,
                                           patExtractTraverser: => PatExtractTraverser,
                                           patExtractInfixTraverser: => PatExtractInfixTraverser,
                                           patInterpolateTraverser: => PatInterpolateTraverser,
                                           patXmlTraverser: => PatXmlTraverser,
                                           patTypedTraverser: => PatTypedTraverser) extends PatTraverser {

  override def traverse(pat: Pat): Unit = pat match {
    case lit: Lit => LitTraverser.traverse(lit)
    case termName: Term.Name => termNameTraverser.traverse(termName)
    case patternWildcard: Pat.Wildcard => patWildcardTraverser.traverse(patternWildcard)
    case patternSeqWildcard: Pat.SeqWildcard => patSeqWildcardTraverser.traverse(patternSeqWildcard)
    case patternVar: Pat.Var => patVarTraverser.traverse(patternVar)
    case patternBind: Bind => bindTraverser.traverse(patternBind)
    case patternAlternative: Alternative => alternativeTraverser.traverse(patternAlternative)
    case patternTuple: Pat.Tuple => patTupleTraverser.traverse(patternTuple)
    case patternExtract: Pat.Extract => patExtractTraverser.traverse(patternExtract)
    case patternExtractInfix: Pat.ExtractInfix => patExtractInfixTraverser.traverse(patternExtractInfix)
    case patternInterpolate: Pat.Interpolate => patInterpolateTraverser.traverse(patternInterpolate)
    case patternXml: Pat.Xml => patXmlTraverser.traverse(patternXml)
    case patternTyped: Pat.Typed => patTypedTraverser.traverse(patternTyped)
    case _ => emitComment(s"UNSUPPORTED: $pat")
  }
}

object PatTraverser extends PatTraverserImpl(
  TermNameTraverser,
  PatWildcardTraverser,
  PatSeqWildcardTraverser,
  PatVarTraverser,
  BindTraverser,
  AlternativeTraverser,
  PatTupleTraverser,
  PatExtractTraverser,
  PatExtractInfixTraverser,
  PatInterpolateTraverser,
  PatXmlTraverser,
  PatTypedTraverser
)
