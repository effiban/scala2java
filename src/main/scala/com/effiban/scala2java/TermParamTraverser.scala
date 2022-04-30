package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.{emit, emitModifiers}
import com.effiban.scala2java.TraversalContext.javaOwnerContext

import scala.meta.Mod.Final
import scala.meta.Term

trait TermParamTraverser extends ScalaTreeTraverser[Term.Param]

private[scala2java] class TermParamTraverserImpl(annotListTraverser: => AnnotListTraverser,
                                                 typeTraverser: => TypeTraverser,
                                                 nameTraverser: => NameTraverser,
                                                 javaModifiersResolver: JavaModifiersResolver) extends TermParamTraverser {

  // method parameter declaration
  override def traverse(termParam: Term.Param): Unit = {
    annotListTraverser.traverseMods(termParam.mods, onSameLine = true)
    val mods = javaOwnerContext match {
      case Lambda => termParam.mods
      case _ => termParam.mods :+ Final()
    }
    val modifierNames = javaModifiersResolver.resolve(mods, List(classOf[Final]))
    emitModifiers(modifierNames)
    termParam.decltpe.foreach(declType => {
      typeTraverser.traverse(declType)
      emit(" ")
    })
    nameTraverser.traverse(termParam.name)
  }
}

object TermParamTraverser extends TermParamTraverserImpl(
  AnnotListTraverser,
  TypeTraverser,
  NameTraverser,
  JavaModifiersResolver
)