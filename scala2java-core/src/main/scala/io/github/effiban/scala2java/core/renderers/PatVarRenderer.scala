package io.github.effiban.scala2java.core.renderers

import scala.meta.Pat

trait PatVarRenderer extends JavaTreeRenderer[Pat.Var]

private[renderers] class PatVarRendererImpl(termNameRenderer: TermNameRenderer) extends PatVarRenderer {

  // Pattern match variable, e.g. `a` in case a =>
  override def render(patternVar: Pat.Var): Unit = {
    termNameRenderer.render(patternVar.name)
  }
}
