package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emitComment

import scala.meta.Pat.{Alternative, Bind}
import scala.meta.{Lit, Pat, Term}

object PatTraverser extends ScalaTreeTraverser[Pat] {

  override def traverse(pat: Pat): Unit = pat match {
    case lit: Lit => LitTraverser.traverse(lit)
    case termName: Term.Name => TermNameTraverser.traverse(termName)
    case patternWildcard: Pat.Wildcard => PatWildcardTraverser.traverse(patternWildcard)
    case patternSeqWildcard: Pat.SeqWildcard => PatSeqWildcardTraverser.traverse(patternSeqWildcard)
    case patternVar: Pat.Var => PatVarTraverser.traverse(patternVar)
    case patternBind: Bind => BindTraverser.traverse(patternBind)
    case patternAlternative: Alternative => AlternativeTraverser.traverse(patternAlternative)
    case patternTuple: Pat.Tuple => PatTupleTraverser.traverse(patternTuple)
    case patternExtract: Pat.Extract => PatExtractTraverser.traverse(patternExtract)
    case patternExtractInfix: Pat.ExtractInfix => PatExtractInfixTraverser.traverse(patternExtractInfix)
    case patternInterpolate: Pat.Interpolate => PatInterpolateTraverser.traverse(patternInterpolate)
    case patternXml: Pat.Xml => PatXmlTraverser.traverse(patternXml)
    case patternTyped: Pat.Typed => PatTypedTraverser.traverse(patternTyped)
    case _ => emitComment(s"UNSUPPORTED: $pat")
  }
}
