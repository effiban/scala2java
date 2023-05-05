package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Pat.Alternative
import scala.meta.{Lit, Pat, Term}

trait PatRenderer extends JavaTreeRenderer[Pat]

private[renderers] class PatRendererImpl(litRenderer: LitRenderer,
                                         termNameRenderer: TermNameRenderer,
                                         patWildcardRenderer: PatWildcardRenderer,
                                         patVarRenderer: PatVarRenderer)
                                        (implicit javaWriter: JavaWriter) extends PatRenderer {

  import javaWriter._

  override def render(pat: Pat): Unit = pat match {
    case lit: Lit => litRenderer.render(lit)
    case termName: Term.Name => termNameRenderer.render(termName)
    case patternWildcard: Pat.Wildcard => patWildcardRenderer.render(patternWildcard)
    case patternVar: Pat.Var => patVarRenderer.render(patternVar)
    case patternAlternative: Alternative => // TODO
    case patternTyped: Pat.Typed => // TODO
    case _ => writeComment(s"UNSUPPORTED: $pat")
  }
}
