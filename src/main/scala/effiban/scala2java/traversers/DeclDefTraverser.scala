package effiban.scala2java.traversers

import effiban.scala2java.contexts.{JavaModifiersContext, StatContext}
import effiban.scala2java.entities.JavaTreeType
import effiban.scala2java.entities.JavaTreeType.{JavaTreeType, Method}
import effiban.scala2java.resolvers.JavaModifiersResolver
import effiban.scala2java.writers.JavaWriter

import scala.meta.{Decl, Type}

trait DeclDefTraverser {
  def traverse(defDecl: Decl.Def, context: StatContext = StatContext()): Unit
}

private[traversers] class DeclDefTraverserImpl(annotListTraverser: => AnnotListTraverser,
                                               typeParamListTraverser: => TypeParamListTraverser,
                                               typeTraverser: => TypeTraverser,
                                               termNameTraverser: => TermNameTraverser,
                                               termParamListTraverser: => TermParamListTraverser,
                                               javaModifiersResolver: JavaModifiersResolver)
                                              (implicit javaWriter: JavaWriter) extends DeclDefTraverser {

  import javaWriter._

  override def traverse(defDecl: Decl.Def, context: StatContext = StatContext()): Unit = {
    writeLine()
    annotListTraverser.traverseMods(defDecl.mods)
    writeModifiers(resolveJavaModifiers(defDecl, context.javaScope))
    traverseTypeParams(defDecl.tparams)
    typeTraverser.traverse(defDecl.decltpe)
    write(" ")
    termNameTraverser.traverse(defDecl.name)

    termParamListTraverser.traverse(termParams = defDecl.paramss.flatten, context = StatContext(Method))
  }

  private def resolveJavaModifiers(defDecl: Decl.Def, parentJavaScope: JavaTreeType) = {
    val javaModifiersContext = JavaModifiersContext(
      scalaTree = defDecl,
      scalaMods = defDecl.mods,
      javaTreeType = JavaTreeType.Method,
      javaScope = parentJavaScope
    )
    javaModifiersResolver.resolve(javaModifiersContext)
  }

  private def traverseTypeParams(tparams: List[Type.Param]): Unit = {
    tparams match {
      case Nil =>
      case typeParams =>
        typeParamListTraverser.traverse(typeParams)
        write(" ")
    }
  }
}
