package com.effiban.scala2java

import com.effiban.scala2java.GenericTreeTraverser.{resolveJavaClassMethodExplicitModifiers, resolveJavaInterfaceMethodExplicitModifiers, traverseAnnotations}
import com.effiban.scala2java.JavaEmitter._
import com.effiban.scala2java.TraversalContext.javaOwnerContext

import scala.meta.Mod.Annot
import scala.meta.Term.Block
import scala.meta.{Defn, Stat}

object DefnDefTraverser extends ScalaTreeTraverser[Defn.Def] {
  def traverse(defDef: Defn.Def): Unit = {
    emitLine()
    traverseAnnotations(defDef.mods.collect { case ann: Annot => ann })
    val resolvedModifierNames = javaOwnerContext match {
      case Interface => resolveJavaInterfaceMethodExplicitModifiers(defDef.mods, hasBody = true)
      case Class => resolveJavaClassMethodExplicitModifiers(defDef.mods)
      case _ => Nil
    }
    emitModifiers(resolvedModifierNames)
    defDef.decltpe.foreach(GenericTreeTraverser.traverse)
    emit(" ")
    GenericTreeTraverser.traverse(defDef.name)
    // TODO handle method type params

    val outerJavaOwnerContext = javaOwnerContext
    javaOwnerContext = Method
    traverseMethodParamsAndBody(defDef)
    javaOwnerContext = outerJavaOwnerContext
  }

  private def traverseMethodParamsAndBody(defDef: Defn.Def): Unit = {
    traverseMethodParams(defDef)
    // method body
    defDef.body match {
      case block: Block => GenericTreeTraverser.traverse(block)
      case stmt: Stat =>
        emitBlockStart()
        GenericTreeTraverser.traverseLastStatement(stmt)
        emitBlockEnd()
      case _ => emitStatementEnd()
    }
  }

  def traverseMethodParams(defDef: Defn.Def): Unit = {
    emitParametersStart()
    val params = defDef.paramss.flatten
    GenericTreeTraverser.traverse(params)
    emitParametersEnd()
  }
}
