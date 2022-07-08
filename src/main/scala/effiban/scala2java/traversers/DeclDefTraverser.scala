package effiban.scala2java.traversers

import effiban.scala2java
import effiban.scala2java.TraversalContext.javaOwnerContext
import effiban.scala2java.{Interface, JavaEmitter, JavaModifiersResolver, Method}

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
    val resolvedModifierNames = javaOwnerContext match {
      case Interface => javaModifiersResolver.resolveForInterfaceMethod(defDecl.mods, hasBody = false)
      case scala2java.Class => javaModifiersResolver.resolveForClassMethod(defDecl.mods)
      case _ => Nil
    }
    emitModifiers(resolvedModifierNames)
    typeTraverser.traverse(defDecl.decltpe)
    emit(" ")
    termNameTraverser.traverse(defDecl.name)
    //TODO handle method type params

    val outerJavaOwnerContext = javaOwnerContext
    javaOwnerContext = Method
    termParamListTraverser.traverse(defDecl.paramss.flatten)
    javaOwnerContext = outerJavaOwnerContext
  }
}

object DeclDefTraverser extends DeclDefTraverserImpl(
  AnnotListTraverser,
  TypeTraverser,
  TermNameTraverser,
  TermParamListTraverser,
  JavaModifiersResolver
)
