package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts._
import io.github.effiban.scala2java.core.entities.Decision.{No, Uncertain, Yes}
import io.github.effiban.scala2java.core.entities.JavaTreeType
import io.github.effiban.scala2java.core.entities.TraversalConstants.UnknownType
import io.github.effiban.scala2java.core.renderers._
import io.github.effiban.scala2java.core.renderers.contextfactories.ModifiersRenderContextFactory
import io.github.effiban.scala2java.core.typeinference.TermTypeInferrer
import io.github.effiban.scala2java.core.writers.JavaWriter
import io.github.effiban.scala2java.spi.entities.JavaScope.MethodSignature
import io.github.effiban.scala2java.spi.transformers.DefnDefTransformer

import scala.meta.{Defn, Type}

@deprecated
trait DeprecatedDefnDefTraverser {
  def traverse(defnDef: Defn.Def, context: DefnDefContext = DefnDefContext()): Unit
}

@deprecated
private[traversers] class DeprecatedDefnDefTraverserImpl(statModListTraverser: => StatModListTraverser,
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
                                                         blockRenderer: => BlockRenderer,
                                                         termTypeInferrer: => TermTypeInferrer,
                                                         defnDefTransformer: DefnDefTransformer)
                                                        (implicit javaWriter: JavaWriter) extends DeprecatedDefnDefTraverser {

  import javaWriter._

  override def traverse(defnDef: Defn.Def, context: DefnDefContext = DefnDefContext()): Unit = {
    val transformedDefnDef = defnDefTransformer.transform(defnDef)
    writeLine()
    val modListTraversalResult = statModListTraverser.traverse(ModifiersContext(transformedDefnDef, JavaTreeType.Method, context.javaScope))
    val modifiersRenderContext = modifiersRenderContextFactory(modListTraversalResult)
    modListRenderer.render(modifiersRenderContext)
    traverseTypeParams(transformedDefnDef.tparams)
    val maybeMethodType = resolveMethodType(transformedDefnDef)
    traverseMethodType(maybeMethodType)
    termNameRenderer.render(transformedDefnDef.name)
    traverseMethodParamsAndBody(transformedDefnDef, maybeMethodType)
  }

  private def traverseMethodParamsAndBody(defDef: Defn.Def, maybeMethodType: Option[Type]): Unit = {
    val traversedParams = defDef.paramss.flatten.map(param => termParamTraverser.traverse(param, StatContext(MethodSignature)))
    termParamListRenderer.render(traversedParams)
    val shouldReturnValue = maybeMethodType match {
      case Some(Type.Name("Unit") | Type.AnonymousName()) => No
      case Some(_) => Yes
      case None => Uncertain
    }
    val blockContext = BlockContext(shouldReturnValue = shouldReturnValue)
    val traversedBody = blockWrappingTermTraverser.traverse(term = defDef.body, context = blockContext)
    blockRenderer.render(traversedBody, BlockRenderContext(uncertainReturn = maybeMethodType.isEmpty))
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
