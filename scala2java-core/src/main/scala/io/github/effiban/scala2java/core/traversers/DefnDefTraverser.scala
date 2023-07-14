package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts._
import io.github.effiban.scala2java.core.entities.Decision.{No, Uncertain, Yes}
import io.github.effiban.scala2java.core.entities.JavaTreeType
import io.github.effiban.scala2java.core.traversers.results.DefnDefTraversalResult
import io.github.effiban.scala2java.core.typeinference.TermTypeInferrer
import io.github.effiban.scala2java.spi.entities.JavaScope.MethodSignature
import io.github.effiban.scala2java.spi.transformers.DefnDefTransformer

import scala.meta.{Defn, Type}

trait DefnDefTraverser {
  def traverse(defnDef: Defn.Def, context: DefnDefContext = DefnDefContext()): DefnDefTraversalResult
}

private[traversers] class DefnDefTraverserImpl(statModListTraverser: => StatModListTraverser,
                                               typeParamTraverser: => TypeParamTraverser,
                                               typeTraverser: => TypeTraverser,
                                               termParamTraverser: => TermParamTraverser,
                                               blockWrappingTermTraverser: => BlockWrappingTermTraverser,
                                               termTypeInferrer: => TermTypeInferrer,
                                               defnDefTransformer: DefnDefTransformer) extends DefnDefTraverser {

  override def traverse(defnDef: Defn.Def, context: DefnDefContext = DefnDefContext()): DefnDefTraversalResult = {
    val transformedDefnDef = defnDefTransformer.transform(defnDef)
    val modListTraversalResult = traverseMods(context, transformedDefnDef)
    val traversedTypeParams = transformedDefnDef.tparams.map(typeParamTraverser.traverse)
    val maybeMethodType = resolveMethodType(transformedDefnDef)
    val maybeTraversedMethodType = traverseMethodType(maybeMethodType)
    val traversedMethodParamss = traverseMethodParams(transformedDefnDef)
    val traversedBody = traverseBody(transformedDefnDef, maybeMethodType)

    val traversedDefnDef = Defn.Def(
      mods = modListTraversalResult.scalaMods,
      name = transformedDefnDef.name,
      tparams = traversedTypeParams,
      paramss = traversedMethodParamss,
      decltpe = maybeTraversedMethodType,
      body = traversedBody)

    DefnDefTraversalResult(tree = traversedDefnDef, javaModifiers = modListTraversalResult.javaModifiers)
  }

  private def traverseMods(context: DefnDefContext, transformedDefnDef: Defn.Def) = {
    statModListTraverser.traverse(ModifiersContext(transformedDefnDef, JavaTreeType.Method, context.javaScope))
  }

  private def traverseMethodParams(defDef: Defn.Def) = {
    defDef.paramss.map(params => params.map(param => termParamTraverser.traverse(param, StatContext(MethodSignature))))
  }

  private def traverseBody(defDef: Defn.Def, maybeMethodType: Option[Type]) = {
    val shouldReturnValue = maybeMethodType match {
      case Some(Type.Name("Unit") | Type.AnonymousName()) => No
      case Some(_) => Yes
      case None => Uncertain
    }
    val blockContext = BlockContext(shouldReturnValue = shouldReturnValue)
    blockWrappingTermTraverser.traverse(term = defDef.body, context = blockContext)
  }

  private def resolveMethodType(defnDef: Defn.Def) = {
    defnDef.decltpe match {
      case Some(tpe) => Some(tpe)
      case None => termTypeInferrer.infer(defnDef.body)
    }
  }

  private def traverseMethodType(maybeType: Option[Type]): Option[Type] = {
    maybeType map {
      case Type.AnonymousName() => Type.AnonymousName()
      case tpe => typeTraverser.traverse(tpe)
    }
  }
}
