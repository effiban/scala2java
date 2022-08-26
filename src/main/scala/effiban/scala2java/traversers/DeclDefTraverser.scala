package effiban.scala2java.traversers

import effiban.scala2java.entities.JavaTreeType
import effiban.scala2java.entities.JavaTreeType.{Interface, Method}
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
    val resolvedModifierNames = javaScope match {
      case Interface => javaModifiersResolver.resolveForInterfaceMethod(defDecl.mods, hasBody = false)
      case JavaTreeType.Class => javaModifiersResolver.resolveForClassMethod(defDecl.mods)
      case _ => Nil
    }
    writeModifiers(resolvedModifierNames)
    traverseTypeParams(defDecl.tparams)
    typeTraverser.traverse(defDecl.decltpe)
    write(" ")
    termNameTraverser.traverse(defDecl.name)

    val outerJavaScope = javaScope
    javaScope = Method
    termParamListTraverser.traverse(defDecl.paramss.flatten)
    javaScope = outerJavaScope
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
