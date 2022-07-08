package effiban.scala2java.traversers

import effiban.scala2java.JavaEmitter
import effiban.scala2java.entities.JavaScope
import effiban.scala2java.entities.JavaScope.{Interface, Method}
import effiban.scala2java.entities.TraversalContext.javaScope
import effiban.scala2java.resolvers.JavaModifiersResolver

import scala.meta.Decl

trait DeclDefTraverser extends ScalaTreeTraverser[Decl.Def]

private[scala2java] class DeclDefTraverserImpl(annotListTraverser: => AnnotListTraverser,
                                               typeTraverser: => TypeTraverser,
                                               termNameTraverser: => TermNameTraverser,
                                               termParamListTraverser: => TermParamListTraverser,
                                               javaModifiersResolver: JavaModifiersResolver)
                                              (implicit javaEmitter: JavaEmitter) extends DeclDefTraverser {

  import javaEmitter._

  override def traverse(defDecl: Decl.Def): Unit = {
    emitLine()
    annotListTraverser.traverseMods(defDecl.mods)
    val resolvedModifierNames = javaScope match {
      case Interface => javaModifiersResolver.resolveForInterfaceMethod(defDecl.mods, hasBody = false)
      case JavaScope.Class => javaModifiersResolver.resolveForClassMethod(defDecl.mods)
      case _ => Nil
    }
    emitModifiers(resolvedModifierNames)
    typeTraverser.traverse(defDecl.decltpe)
    emit(" ")
    termNameTraverser.traverse(defDecl.name)
    //TODO handle method type params

    val outerJavaScope = javaScope
    javaScope = Method
    termParamListTraverser.traverse(defDecl.paramss.flatten)
    javaScope = outerJavaScope
  }
}

object DeclDefTraverser extends DeclDefTraverserImpl(
  AnnotListTraverser,
  TypeTraverser,
  TermNameTraverser,
  TermParamListTraverser,
  JavaModifiersResolver
)
