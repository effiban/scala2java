package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.classifiers.{JavaStatClassifier, TermTreeClassifier}
import io.github.effiban.scala2java.core.entities.JavaModifier
import io.github.effiban.scala2java.core.entities.TraversalConstants.UncertainReturn
import io.github.effiban.scala2java.core.renderers.contexts.{BlockStatRenderContext, IfRenderContext, TryRenderContext, VarRenderContext}
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Mod.Final
import scala.meta.Term.{If, Try, TryWithHandler}
import scala.meta.{Decl, Defn, Stat, Term}

trait BlockStatRenderer {

  def render(stat: Stat): Unit

  def renderLast(stat: Stat, blockStatRenderContext: BlockStatRenderContext = BlockStatRenderContext()): Unit
}

private[renderers] class BlockStatRendererImpl(statTermRenderer: => StatTermRenderer,
                                               ifRenderer: => IfRenderer,
                                               tryRenderer: => TryRenderer,
                                               tryWithHandlerRenderer: => TryWithHandlerRenderer,
                                               defnVarRenderer: => DefnVarRenderer,
                                               declVarRenderer: => DeclVarRenderer,
                                               termTreeClassifier: TermTreeClassifier,
                                               javaStatClassifier: JavaStatClassifier)
                                              (implicit javaWriter: JavaWriter) extends BlockStatRenderer {

  import javaWriter._

  override def render(stat: Stat): Unit = {
    stat match {
      case term: Term => statTermRenderer.render(term)
      case defnVar: Defn.Var =>
        val javaModifiers = resolveDefnVarJavaModifiers(defnVar)
        defnVarRenderer.render(defnVar, VarRenderContext(javaModifiers, inBlock = true))
      case declVar: Decl.Var => declVarRenderer.render(declVar, VarRenderContext(inBlock = true))
      // TODO support other stats once renderers are ready
      case aStat: Stat => throw new UnsupportedOperationException(s"Rendering of $aStat in a block is not supported yet")
    }
    writeStatEnd(stat)
  }

  private def resolveDefnVarJavaModifiers(defnVar: Defn.Var): List[JavaModifier] = {
    defnVar.mods.flatMap {
      case _: Final => Some(JavaModifier.Final)
      case _ => None
    }
  }

  override def renderLast(stat: Stat, context: BlockStatRenderContext = BlockStatRenderContext()): Unit = {
    stat match {
      case `if`: If => ifRenderer.render(`if`, IfRenderContext(context.uncertainReturn))
      case `try`: Try  => tryRenderer.render(`try`, TryRenderContext(context.uncertainReturn))
      case tryWithHandler: TryWithHandler => tryWithHandlerRenderer.render(tryWithHandler, TryRenderContext(context.uncertainReturn))
      case term: Term if context.uncertainReturn && termTreeClassifier.isReturnable(term) =>
        writeComment(UncertainReturn)
        render(stat)
      case _ => render(stat)
    }
  }

  private def writeStatEnd(stat: Stat): Unit = {
    if (javaStatClassifier.requiresEndDelimiter(stat)) {
      writeStatementEnd()
    }
  }
}
