package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.renderers.contexts.{DeclRenderContext, DefRenderContext, VarRenderContext}
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Decl

trait DeclRenderer {
  def render(decl: Decl, context: DeclRenderContext): Unit
}

private[renderers] class DeclRendererImpl(declVarRenderer: => DeclVarRenderer,
                                          declDefRenderer: => DeclDefRenderer)
                                         (implicit javaWriter: JavaWriter) extends DeclRenderer {

  import javaWriter._

  override def render(decl: Decl, context: DeclRenderContext): Unit =
    (decl, context) match {
      case (declVar: Decl.Var, varContext: VarRenderContext) => declVarRenderer.render(declVar, varContext)
      case (declVar: Decl.Var, aContext) => handleInvalidContext(declVar, aContext)

      case (declDef: Decl.Def, defContext: DefRenderContext) => declDefRenderer.render(declDef, defContext)
      case (declDef: Decl.Def, aContext) => handleInvalidContext(declDef, aContext)

      case _ => writeComment(s"UNSUPPORTED: $decl")
    }

  private def handleInvalidContext(decl: Decl, aContext: DeclRenderContext): Unit = {
    throw new IllegalStateException(s"Got an invalid context type $aContext for: $decl")
  }
}
