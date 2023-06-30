package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.classifiers.JavaStatClassifier
import io.github.effiban.scala2java.core.contexts._
import io.github.effiban.scala2java.core.entities.JavaModifier.Final
import io.github.effiban.scala2java.core.entities.TraversalConstants.UncertainReturn
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Term.{If, Try, TryWithHandler}
import scala.meta.{Decl, Defn, Stat, Term}

trait BlockStatRenderer {

  def render(stat: Stat): Unit

  def renderLast(stat: Stat, blockStatRenderContext: BlockStatRenderContext = SimpleBlockStatRenderContext()): Unit
}

private[renderers] class BlockStatRendererImpl(expressionTermRefRenderer: => ExpressionTermRefRenderer,
                                               ifRenderer: => IfRenderer,
                                               tryRenderer: => TryRenderer,
                                               tryWithHandlerRenderer: => TryWithHandlerRenderer,
                                               defaultTermRenderer: => DefaultTermRenderer,
                                               defnValRenderer: => DefnValRenderer,
                                               defnVarRenderer: => DefnVarRenderer,
                                               declVarRenderer: => DeclVarRenderer,
                                               javaStatClassifier: JavaStatClassifier)
                                              (implicit javaWriter: JavaWriter) extends BlockStatRenderer {

  import javaWriter._

  override def render(stat: Stat): Unit = {
    stat match {
      case termRef: Term.Ref => expressionTermRefRenderer.render(termRef)
      case aTerm: Term => defaultTermRenderer.render(aTerm)
      case defnVal: Defn.Val => defnValRenderer.render(defnVal, ValOrVarRenderContext(javaModifiers = List(Final), inBlock = true))
      case defnVar: Defn.Var => defnVarRenderer.render(defnVar, ValOrVarRenderContext(inBlock = true))
      case declVar: Decl.Var => declVarRenderer.render(declVar, ValOrVarRenderContext(inBlock = true))
      // TODO support other stats once renderers are ready
      case aStat: Stat => throw new UnsupportedOperationException(s"Rendering of $aStat in a block is not supported yet")
    }
    writeStatEnd(stat)
  }

  override def renderLast(stat: Stat, context: BlockStatRenderContext = SimpleBlockStatRenderContext()): Unit = {
    (stat, context) match {
      case (`if`: If, anIfContext: IfRenderContext) => ifRenderer.render(`if`, anIfContext)
      case (`try`: Try, tryContext: TryRenderContext)  => tryRenderer.render(`try`, tryContext)
      case (tryWithHandler: TryWithHandler, tryContext: TryRenderContext) => tryWithHandlerRenderer.render(tryWithHandler, tryContext)
      case (_, context: SimpleBlockStatRenderContext) if context.uncertainReturn =>
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
