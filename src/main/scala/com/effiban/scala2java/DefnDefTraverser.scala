package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter._
import com.effiban.scala2java.TraversalContext.javaOwnerContext

import scala.meta.Term.Block
import scala.meta.{Defn, Term, Type}

trait DefnDefTraverser extends ScalaTreeTraverser[Defn.Def]

object DefnDefTraverser extends DefnDefTraverser {

  override def traverse(defDef: Defn.Def): Unit = {
    emitLine()
    AnnotListTraverser.traverseMods(defDef.mods)
    val resolvedModifierNames = javaOwnerContext match {
      case Interface => JavaModifiersResolver.resolveForInterfaceMethod(defDef.mods, hasBody = true)
      case Class => JavaModifiersResolver.resolveForClassMethod(defDef.mods)
      case _ => Nil
    }
    emitModifiers(resolvedModifierNames)
    defDef.decltpe.foreach(TypeTraverser.traverse)
    emit(" ")
    TermNameTraverser.traverse(defDef.name)
    // TODO handle method type params

    val outerJavaOwnerContext = javaOwnerContext
    javaOwnerContext = Method
    traverseMethodParamsAndBody(defDef)
    javaOwnerContext = outerJavaOwnerContext
  }

  private def traverseMethodParamsAndBody(defDef: Defn.Def): Unit = {
    TermParamListTraverser.traverse(defDef.paramss.flatten)
    val withReturnValue = defDef.decltpe match {
      case Some(Type.Name("Unit")) => false
      case Some(_) => true
      // Taking a "reasonable" chance here - if the Scala method has no declared type and inferred type is void,
      // there will be an incorrect 'return' (as opposed to the opposite case when it would be missing)
      case None => true
    }
    defDef.body match {
      case block: Block => BlockTraverser.traverse(block = block, shouldReturnValue = withReturnValue)
      case term: Term => BlockTraverser.traverse(block = Block(List(term)), shouldReturnValue = withReturnValue)
    }
  }
}
