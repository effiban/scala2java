package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{ModifiersContext, StatContext}
import io.github.effiban.scala2java.core.entities.JavaTreeType
import io.github.effiban.scala2java.core.traversers.results.DeclDefTraversalResult
import io.github.effiban.scala2java.spi.entities.JavaScope.MethodSignature

import scala.meta.Decl

trait DeclDefTraverser {
  def traverse(defDecl: Decl.Def, context: StatContext = StatContext()): DeclDefTraversalResult
}

private[traversers] class DeclDefTraverserImpl(statModListTraverser: => StatModListTraverser,
                                               typeParamTraverser: => TypeParamTraverser,
                                               typeTraverser: => TypeTraverser,
                                               termParamTraverser: => TermParamTraverser) extends DeclDefTraverser {

  override def traverse(declDef: Decl.Def, context: StatContext = StatContext()): DeclDefTraversalResult = {
    val modListTraversalResult = traverseMods(declDef, context)
    val traversedTypeParams = declDef.tparams.map(typeParamTraverser.traverse)
    val traversedType = typeTraverser.traverse(declDef.decltpe)
    val traversedMethodParamss = traverseMethodParams(declDef)

    val traversedDeclDef = Decl.Def(
      mods = modListTraversalResult.scalaMods,
      name = declDef.name,
      tparams = traversedTypeParams,
      paramss = traversedMethodParamss,
      decltpe = traversedType)

    DeclDefTraversalResult(tree = traversedDeclDef, javaModifiers = modListTraversalResult.javaModifiers)
  }

  private def traverseMods(declDef: Decl.Def, context: StatContext) = {
    statModListTraverser.traverse(ModifiersContext(declDef, JavaTreeType.Method, context.javaScope))
  }
  private def traverseMethodParams(declDef: Decl.Def) = {
    declDef.paramss.map(params => params.map(param => termParamTraverser.traverse(param, StatContext(MethodSignature))))
  }
}
