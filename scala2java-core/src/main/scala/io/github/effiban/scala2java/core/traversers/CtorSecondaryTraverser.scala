package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts._
import io.github.effiban.scala2java.core.entities.JavaTreeType
import io.github.effiban.scala2java.core.renderers._
import io.github.effiban.scala2java.core.renderers.contextfactories.ModifiersRenderContextFactory
import io.github.effiban.scala2java.core.writers.JavaWriter
import io.github.effiban.scala2java.spi.entities.JavaScope

import scala.meta.{Ctor, Stat}

trait CtorSecondaryTraverser {
  def traverse(secondaryCtor: Ctor.Secondary, ctorContext: CtorContext): Unit
}

private[traversers] class CtorSecondaryTraverserImpl(modListTraverser: => ModListTraverser,
                                                     modifiersRenderContextFactory: ModifiersRenderContextFactory,
                                                     modListRenderer: => ModListRenderer,
                                                     typeNameTraverser: => TypeNameTraverser,
                                                     typeNameRenderer: TypeNameRenderer,
                                                     termParamTraverser: => TermParamTraverser,
                                                     termParamListRenderer: => TermParamListRenderer,
                                                     initTraverser: => InitTraverser,
                                                     initRenderer: => InitRenderer,
                                                     blockStatTraverser: => BlockStatTraverser,
                                                     blockStatRenderer: => BlockStatRenderer)
                                                    (implicit javaWriter: JavaWriter)
  extends CtorSecondaryTraverser {

  import javaWriter._

  override def traverse(secondaryCtor: Ctor.Secondary, context: CtorContext): Unit = {
    writeLine()
    traverseMods(secondaryCtor, context)
    traverseClassName(context)
    traverseParams(secondaryCtor)
    traverseBody(secondaryCtor)
  }

  private def traverseMods(secondaryCtor: Ctor.Secondary, context: CtorContext): Unit = {
    val modListTraversalResult = modListTraverser.traverse(ModifiersContext(secondaryCtor, JavaTreeType.Method, context.javaScope))
    val modifiersRenderContext = modifiersRenderContextFactory(modListTraversalResult)
    modListRenderer.render(modifiersRenderContext)
  }

  private def traverseClassName(context: CtorContext): Unit = {
    val traversedClassName = typeNameTraverser.traverse(context.className)
    typeNameRenderer.render(traversedClassName)
  }

  private def traverseParams(secondaryCtor: Ctor.Secondary): Unit = {
    val methodParamTraversalResults = secondaryCtor.paramss.flatten.map(param => termParamTraverser.traverse(param, StatContext(JavaScope.MethodSignature)))
    // We can assume the Java modifiers in the results are all the same (all 'final') so we can combine them
    val paramListRenderContext = TermParamListRenderContext(javaModifiers = methodParamTraversalResults.flatMap(_.javaModifiers).distinct)
    termParamListRenderer.render(methodParamTraversalResults.map(_.tree), paramListRenderContext)
  }

  private def traverseBody(secondaryCtor: Ctor.Secondary): Unit = {
    writeBlockStart()
    val traversedInit = initTraverser.traverse(secondaryCtor.init)
    initRenderer.render(traversedInit, InitContext(argNameAsComment = true))
    writeStatementEnd()
    traverseContents(secondaryCtor.stats)
    writeBlockEnd()
  }

  private def traverseContents(stats: List[Stat]): Unit = {
    if (stats.nonEmpty) {
      val traversedStats = stats.map(blockStatTraverser.traverse)
      traversedStats.foreach(blockStatRenderer.render)
    }
  }
}
