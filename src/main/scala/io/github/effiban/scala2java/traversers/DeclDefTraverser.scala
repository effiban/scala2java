package io.github.effiban.scala2java.traversers

import io.github.effiban.scala2java.contexts.{JavaModifiersContext, StatContext}
import io.github.effiban.scala2java.entities.JavaScope.JavaScope
import io.github.effiban.scala2java.entities.{JavaScope, JavaTreeType}
import io.github.effiban.scala2java.writers.JavaWriter

import scala.meta.{Decl, Type}

trait DeclDefTraverser {
  def traverse(defDecl: Decl.Def, context: StatContext = StatContext()): Unit
}

private[traversers] class DeclDefTraverserImpl(modListTraverser: => ModListTraverser,
                                               typeParamListTraverser: => TypeParamListTraverser,
                                               typeTraverser: => TypeTraverser,
                                               termNameTraverser: => TermNameTraverser,
                                               termParamListTraverser: => TermParamListTraverser)
                                              (implicit javaWriter: JavaWriter) extends DeclDefTraverser {

  import javaWriter._

  override def traverse(defDecl: Decl.Def, context: StatContext = StatContext()): Unit = {
    writeLine()
    modListTraverser.traverse(toJavaModifiersContext(defDecl, context.javaScope))
    traverseTypeParams(defDecl.tparams)
    typeTraverser.traverse(defDecl.decltpe)
    write(" ")
    termNameTraverser.traverse(defDecl.name)
    termParamListTraverser.traverse(termParams = defDecl.paramss.flatten, context = StatContext(JavaScope.MethodSignature))
  }

  private def toJavaModifiersContext(defDecl: Decl.Def, parentJavaScope: JavaScope) =
    JavaModifiersContext(
      scalaTree = defDecl,
      scalaMods = defDecl.mods,
      javaTreeType = JavaTreeType.Method,
      javaScope = parentJavaScope
    )

  private def traverseTypeParams(tparams: List[Type.Param]): Unit = {
    tparams match {
      case Nil =>
      case typeParams =>
        typeParamListTraverser.traverse(typeParams)
        write(" ")
    }
  }
}
