package com.effiban.scala2java

import com.effiban.scala2java.TraversalContext.javaOwnerContext

import scala.meta.Term.Block
import scala.meta.{Defn, Term, Type}

trait DefnDefTraverser extends ScalaTreeTraverser[Defn.Def]

private[scala2java] class DefnDefTraverserImpl(annotListTraverser: => AnnotListTraverser,
                                               termNameTraverser: => TermNameTraverser,
                                               typeTraverser: => TypeTraverser,
                                               termParamListTraverser: => TermParamListTraverser,
                                               blockTraverser: => BlockTraverser,
                                               javaModifiersResolver: JavaModifiersResolver)
                                              (implicit javaEmitter: JavaEmitter) extends DefnDefTraverser {

  import javaEmitter._

  override def traverse(defDef: Defn.Def): Unit = {
    emitLine()
    annotListTraverser.traverseMods(defDef.mods)
    val resolvedModifierNames = javaOwnerContext match {
      case Interface => javaModifiersResolver.resolveForInterfaceMethod(defDef.mods, hasBody = true)
      case Class => javaModifiersResolver.resolveForClassMethod(defDef.mods)
      case _ => Nil
    }
    emitModifiers(resolvedModifierNames)
    defDef.decltpe.foreach(typeTraverser.traverse)
    emit(" ")
    termNameTraverser.traverse(defDef.name)
    // TODO handle method type params

    val outerJavaOwnerContext = javaOwnerContext
    javaOwnerContext = Method
    traverseMethodParamsAndBody(defDef)
    javaOwnerContext = outerJavaOwnerContext
  }

  private def traverseMethodParamsAndBody(defDef: Defn.Def): Unit = {
    termParamListTraverser.traverse(defDef.paramss.flatten)
    val withReturnValue = defDef.decltpe match {
      case Some(Type.Name("Unit")) => false
      case Some(_) => true
      // Taking a "reasonable" chance here - if the Scala method has no declared type and inferred type is void,
      // there will be an incorrect 'return' (as opposed to the opposite case when it would be missing)
      case None => true
    }
    defDef.body match {
      case block: Block => blockTraverser.traverse(block = block, shouldReturnValue = withReturnValue)
      case term: Term => blockTraverser.traverse(block = Block(List(term)), shouldReturnValue = withReturnValue)
    }
  }
}

object DefnDefTraverser extends DefnDefTraverserImpl(
  AnnotListTraverser,
  TermNameTraverser,
  TypeTraverser,
  TermParamListTraverser,
  BlockTraverser,
  JavaModifiersResolver
)
