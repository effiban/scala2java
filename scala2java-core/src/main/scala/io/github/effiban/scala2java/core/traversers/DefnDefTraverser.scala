package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{BlockContext, DefnDefContext, ModifiersContext, StatContext}
import io.github.effiban.scala2java.core.entities.Decision.{No, Uncertain, Yes}
import io.github.effiban.scala2java.core.entities.JavaTreeType
import io.github.effiban.scala2java.core.entities.TraversalConstants.UnknownType
import io.github.effiban.scala2java.core.renderers.contextfactories.BlockRenderContextFactory
import io.github.effiban.scala2java.core.renderers.{BlockRenderer, TermNameRenderer, TypeRenderer}
import io.github.effiban.scala2java.core.typeinference.TermTypeInferrer
import io.github.effiban.scala2java.core.writers.JavaWriter
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.spi.transformers.DefnDefTransformer

import scala.meta.{Defn, Init, Type}

trait DefnDefTraverser {
  def traverse(defnDef: Defn.Def, context: DefnDefContext = DefnDefContext()): Unit
}

private[traversers] class DefnDefTraverserImpl(modListTraverser: => DeprecatedModListTraverser,
                                               typeParamListTraverser: => TypeParamListTraverser,
                                               termNameRenderer: TermNameRenderer,
                                               typeTraverser: => TypeTraverser,
                                               typeRenderer: => TypeRenderer,
                                               termParamListTraverser: => DeprecatedTermParamListTraverser,
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
    modListTraverser.traverse(ModifiersContext(transformedDefnDef, JavaTreeType.Method, context.javaScope))
    traverseTypeParams(transformedDefnDef.tparams)
    val maybeMethodType = resolveMethodType(transformedDefnDef)
    traverseMethodType(maybeMethodType)
    termNameRenderer.render(transformedDefnDef.name)
    traverseMethodParamsAndBody(transformedDefnDef, maybeMethodType, context.maybeInit)
  }

  private def traverseMethodParamsAndBody(defDef: Defn.Def, maybeMethodType: Option[Type], maybeInit: Option[Init] = None): Unit = {
    termParamListTraverser.traverse(termParams = defDef.paramss.flatten, context = StatContext(JavaScope.MethodSignature))
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
        typeParamListTraverser.traverse(typeParams)
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
