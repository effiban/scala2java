package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter._
import com.effiban.scala2java.LastStatementTraverser.traverseLastStatement
import com.effiban.scala2java.TraversalContext.javaOwnerContext

import scala.meta.Term.Block
import scala.meta.{Defn, Stat}

object DefnDefTraverser extends ScalaTreeTraverser[Defn.Def] {
  def traverse(defDef: Defn.Def): Unit = {
    emitLine()
    AnnotListTraverser.traverseMods(defDef.mods)
    val resolvedModifierNames = javaOwnerContext match {
      case Interface => JavaModifiersResolver.resolveForInterfaceMethod(defDef.mods, hasBody = true)
      case Class => JavaModifiersResolver.resolveForClassMethod(defDef.mods)
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
    ArgumentListTraverser.traverse(defDef.paramss.flatten, maybeDelimiterType = Some(Parentheses))
    // method body
    defDef.body match {
      case block: Block => GenericTreeTraverser.traverse(block)
      case stmt: Stat =>
        emitBlockStart()
        traverseLastStatement(stmt)
        emitBlockEnd()
      case _ => emitStatementEnd()
    }
  }
}
