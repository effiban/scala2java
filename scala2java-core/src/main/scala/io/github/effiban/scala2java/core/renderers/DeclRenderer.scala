package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.renderers.contexts.{DeclRenderContext, DefRenderContext, VarRenderContext}
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Decl

trait DeclRenderer {
  def render(decl: Decl, context: DeclRenderContext = DeclRenderContext()): Unit
}

private[renderers] class DeclRendererImpl(declVarRenderer: => DeclVarRenderer,
                                          declDefRenderer: => DeclDefRenderer)
                                         (implicit javaWriter: JavaWriter) extends DeclRenderer {

  import javaWriter._

  override def render(decl: Decl, context: DeclRenderContext = DeclRenderContext()): Unit = decl match {
    case declVar: Decl.Var => declVarRenderer.render(declVar, VarRenderContext(context.javaModifiers))
    case declDef: Decl.Def => declDefRenderer.render(declDef, DefRenderContext(context.javaModifiers))
    case _ => writeComment(s"UNSUPPORTED: $decl")
  }
}
