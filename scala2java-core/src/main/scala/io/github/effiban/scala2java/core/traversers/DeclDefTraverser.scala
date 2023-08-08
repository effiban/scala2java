package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.StatContext
import io.github.effiban.scala2java.spi.entities.JavaScope.MethodSignature

import scala.meta.Decl

trait DeclDefTraverser {
  def traverse(defDecl: Decl.Def): Decl.Def
}

private[traversers] class DeclDefTraverserImpl(statModListTraverser: => StatModListTraverser,
                                               typeParamTraverser: => TypeParamTraverser,
                                               typeTraverser: => TypeTraverser,
                                               termParamTraverser: => TermParamTraverser) extends DeclDefTraverser {

  override def traverse(declDef: Decl.Def): Decl.Def = {
    val traversedScalaMods = traverseMods(declDef)
    val traversedTypeParams = declDef.tparams.map(typeParamTraverser.traverse)
    val traversedType = typeTraverser.traverse(declDef.decltpe)
    val traversedMethodParamss = traverseMethodParams(declDef)

    Decl.Def(
      mods = traversedScalaMods,
      name = declDef.name,
      tparams = traversedTypeParams,
      paramss = traversedMethodParamss,
      decltpe = traversedType)
  }

  private def traverseMods(declDef: Decl.Def) = {
    statModListTraverser.traverse(declDef.mods)
  }
  private def traverseMethodParams(declDef: Decl.Def) = {
    declDef.paramss.map(params => params.map(param => termParamTraverser.traverse(param, StatContext(MethodSignature))))
  }
}
