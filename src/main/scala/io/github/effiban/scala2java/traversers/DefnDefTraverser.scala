package io.github.effiban.scala2java.traversers

import io.github.effiban.scala2java.contexts.{BlockContext, DefnDefContext, ModifiersContext, StatContext}
import io.github.effiban.scala2java.entities.Decision.{No, Uncertain, Yes}
import io.github.effiban.scala2java.entities.TraversalConstants.UnknownType
import io.github.effiban.scala2java.entities.{JavaScope, JavaTreeType}
import io.github.effiban.scala2java.typeinference.TermTypeInferrer
import io.github.effiban.scala2java.writers.JavaWriter

import scala.meta.{Defn, Init, Type}

trait DefnDefTraverser {
  def traverse(defnDef: Defn.Def, context: DefnDefContext = DefnDefContext()): Unit
}

private[traversers] class DefnDefTraverserImpl(modListTraverser: => ModListTraverser,
                                               typeParamListTraverser: => TypeParamListTraverser,
                                               termNameTraverser: => TermNameTraverser,
                                               typeTraverser: => TypeTraverser,
                                               termParamListTraverser: => TermParamListTraverser,
                                               blockTraverser: => BlockTraverser,
                                               termTypeInferrer: => TermTypeInferrer)
                                              (implicit javaWriter: JavaWriter) extends DefnDefTraverser {

  import javaWriter._

  override def traverse(defnDef: Defn.Def, context: DefnDefContext = DefnDefContext()): Unit = {
    writeLine()
    modListTraverser.traverse(ModifiersContext(defnDef, JavaTreeType.Method, context.javaScope))
    traverseTypeParams(defnDef.tparams)
    val maybeMethodType = resolveMethodType(defnDef)
    traverseMethodType(maybeMethodType)
    termNameTraverser.traverse(defnDef.name)
    traverseMethodParamsAndBody(defnDef, maybeMethodType, context.maybeInit)
  }

  private def traverseMethodParamsAndBody(defDef: Defn.Def, maybeMethodType: Option[Type], maybeInit: Option[Init] = None): Unit = {
    termParamListTraverser.traverse(termParams = defDef.paramss.flatten, context = StatContext(JavaScope.MethodSignature))
    val shouldReturnValue = maybeMethodType match {
      case Some(Type.Name("Unit") | Type.AnonymousName()) => No
      case Some(_) => Yes
      case None => Uncertain
    }
    val blockContext = BlockContext(shouldReturnValue = shouldReturnValue, maybeInit = maybeInit)
    blockTraverser.traverse(stat = defDef.body, context = blockContext)
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
        typeTraverser.traverse(tpe)
        write(" ")
      case None =>
        writeComment(UnknownType)
        write(" ")
    }
  }
}
