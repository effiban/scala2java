package effiban.scala2java.traversers

import effiban.scala2java.contexts.JavaModifiersContext
import effiban.scala2java.entities.JavaTreeType
import effiban.scala2java.entities.JavaTreeType.Method
import effiban.scala2java.entities.TraversalContext.javaScope
import effiban.scala2java.resolvers.JavaModifiersResolver
import effiban.scala2java.writers.JavaWriter

import scala.meta.{Decl, Type}

trait DeclDefTraverser extends ScalaTreeTraverser[Decl.Def]

private[traversers] class DeclDefTraverserImpl(annotListTraverser: => AnnotListTraverser,
                                               typeParamListTraverser: => TypeParamListTraverser,
                                               typeTraverser: => TypeTraverser,
                                               termNameTraverser: => TermNameTraverser,
                                               termParamListTraverser: => TermParamListTraverser,
                                               javaModifiersResolver: JavaModifiersResolver)
                                              (implicit javaWriter: JavaWriter) extends DeclDefTraverser {

  import javaWriter._

  override def traverse(defDecl: Decl.Def): Unit = {
    writeLine()
    annotListTraverser.traverseMods(defDecl.mods)
    writeModifiers(resolveJavaModifiers(defDecl))
    traverseTypeParams(defDecl.tparams)
    typeTraverser.traverse(defDecl.decltpe)
    write(" ")
    termNameTraverser.traverse(defDecl.name)

    val outerJavaScope = javaScope
    javaScope = Method
    termParamListTraverser.traverse(defDecl.paramss.flatten)
    javaScope = outerJavaScope
  }

  private def resolveJavaModifiers(defDecl: Decl.Def) = {
    val context = JavaModifiersContext(
      scalaTree = defDecl,
      scalaMods = defDecl.mods,
      javaTreeType = JavaTreeType.Method,
      javaScope = javaScope
    )
    javaModifiersResolver.resolve(context)
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
