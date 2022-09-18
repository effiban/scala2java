package effiban.scala2java.traversers

import effiban.scala2java.contexts.{BlockContext, DefnDefContext, JavaModifiersContext, StatContext}
import effiban.scala2java.entities.Decision.{No, Uncertain, Yes}
import effiban.scala2java.entities.JavaScope.JavaScope
import effiban.scala2java.entities.TraversalConstants.UnknownType
import effiban.scala2java.entities.{JavaScope, JavaTreeType}
import effiban.scala2java.resolvers.JavaModifiersResolver
import effiban.scala2java.typeinference.TermTypeInferrer
import effiban.scala2java.writers.JavaWriter

import scala.meta.{Defn, Init, Type}

trait DefnDefTraverser {
  def traverse(defnDef: Defn.Def, context: DefnDefContext = DefnDefContext()): Unit
}

private[traversers] class DefnDefTraverserImpl(annotListTraverser: => AnnotListTraverser,
                                               typeParamListTraverser: => TypeParamListTraverser,
                                               termNameTraverser: => TermNameTraverser,
                                               typeTraverser: => TypeTraverser,
                                               termParamListTraverser: => TermParamListTraverser,
                                               blockTraverser: => BlockTraverser,
                                               termTypeInferrer: => TermTypeInferrer,
                                               javaModifiersResolver: JavaModifiersResolver)
                                              (implicit javaWriter: JavaWriter) extends DefnDefTraverser {

  import javaWriter._

  override def traverse(defnDef: Defn.Def, context: DefnDefContext = DefnDefContext()): Unit = {
    writeLine()
    annotListTraverser.traverseMods(defnDef.mods)
    writeModifiers(resolveJavaModifiers(defnDef, context.javaScope))
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

  private def resolveJavaModifiers(defnDef: Defn.Def, parentJavaScope: JavaScope) = {
    val javaModifiersContext = JavaModifiersContext(
      scalaTree = defnDef,
      scalaMods = defnDef.mods,
      javaTreeType = JavaTreeType.Method,
      javaScope = parentJavaScope
    )
    javaModifiersResolver.resolve(javaModifiersContext)
  }
}
