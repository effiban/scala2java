package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts._
import io.github.effiban.scala2java.core.entities.Decision.{No, Uncertain, Yes}
import io.github.effiban.scala2java.core.entities.JavaTreeType
import io.github.effiban.scala2java.core.entities.TraversalConstants.UnknownType
import io.github.effiban.scala2java.core.renderers._
import io.github.effiban.scala2java.core.renderers.contextfactories.{BlockRenderContextFactory, ModifiersRenderContextFactory}
import io.github.effiban.scala2java.core.typeinference.TermTypeInferrer
import io.github.effiban.scala2java.core.writers.JavaWriter
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.spi.transformers.DefnDefTransformer

import scala.meta.{Defn, Init, Type}

trait DefnDefTraverser {
  def traverse(defnDef: Defn.Def, context: DefnDefContext = DefnDefContext()): Unit
}

private[traversers] class DefnDefTraverserImpl(modListTraverser: => ModListTraverser,
                                               modifiersRenderContextFactory: ModifiersRenderContextFactory,
                                               modListRenderer: => ModListRenderer,
                                               typeParamTraverser: => TypeParamTraverser,
                                               typeParamListRenderer: => TypeParamListRenderer,
                                               termNameRenderer: TermNameRenderer,
                                               typeTraverser: => TypeTraverser,
                                               typeRenderer: => TypeRenderer,
                                               termParamTraverser: => TermParamTraverser,
                                               termParamListRenderer: => TermParamListRenderer,
                                               blockWrappingTermTraverser: => BlockWrappingTermTraverser,
                                               blockRenderContextFactory: => BlockRenderContextFactory,
                                               blockRenderer: => BlockRenderer,
                                               termTypeInferrer: => TermTypeInferrer,
                                               defnDefTransformer: DefnDefTransformer)
                                              (implicit javaWriter: JavaWriter) extends DefnDefTraverser {

  import javaWriter._

  override def traverse(defnDef: Defn.Def, context: DefnDefContext = DefnDefContext()): Unit = {
    val transformedDefnDef = defnDefTransformer.transform(defnDef)
    writeLine()
    val modListTraversalResult = modListTraverser.traverse(ModifiersContext(transformedDefnDef, JavaTreeType.Method, context.javaScope))
    val modifiersRenderContext = modifiersRenderContextFactory(modListTraversalResult)
    modListRenderer.render(modifiersRenderContext)
    traverseTypeParams(transformedDefnDef.tparams)
    val maybeMethodType = resolveMethodType(transformedDefnDef)
    traverseMethodType(maybeMethodType)
    termNameRenderer.render(transformedDefnDef.name)
    traverseMethodParamsAndBody(transformedDefnDef, maybeMethodType, context.maybeInit)
  }

  private def traverseMethodParamsAndBody(defDef: Defn.Def, maybeMethodType: Option[Type], maybeInit: Option[Init] = None): Unit = {
    val methodParamTraversalResults = defDef.paramss.flatten.map(param => termParamTraverser.traverse(param, StatContext(JavaScope.MethodSignature)))
    // We can assume the Java modifiers in the results are all the same (all 'final') so we can combine them
    val paramListRenderContext = TermParamListRenderContext(javaModifiers = methodParamTraversalResults.flatMap(_.javaModifiers).distinct)
    termParamListRenderer.render(methodParamTraversalResults.map(_.tree), paramListRenderContext)
    val shouldReturnValue = maybeMethodType match {
      case Some(Type.Name("Unit") | Type.AnonymousName()) => No
      case Some(_) => Yes
      case None => Uncertain
    }
    val blockContext = BlockContext(shouldReturnValue = shouldReturnValue, maybeInit = maybeInit)
    val blockTraversalResult = blockWrappingTermTraverser.traverse(term = defDef.body, context = blockContext)
    val blockRenderContext = blockRenderContextFactory(blockTraversalResult)
    blockRenderer.render(blockTraversalResult.block, blockRenderContext)
  }

  private def traverseTypeParams(tparams: List[Type.Param]): Unit = {
    tparams match {
      case Nil =>
      case typeParams =>
        val traversedTypeParams = typeParams.map(typeParamTraverser.traverse)
        typeParamListRenderer.render(traversedTypeParams)
        write(" ")
    }
  }

  private def resolveMethodType(defnDef: Defn.Def) = {
    defnDef.decltpe match {
      case Some(tpe) => Some(tpe)
      case None => termTypeInferrer.infer(defnDef.body)
    }
  }

  private def traverseMethodType(maybeType: Option[Type]): Unit = {
    maybeType match {
      case Some(Type.AnonymousName()) =>
      case Some(tpe) =>
        val traversedType = typeTraverser.traverse(tpe)
        typeRenderer.render(traversedType)
        write(" ")
      case None =>
        writeComment(UnknownType)
        write(" ")
    }
  }
}
