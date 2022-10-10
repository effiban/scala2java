package io.github.effiban.scala2java.traversers

import io.github.effiban.scala2java.contexts.{JavaModifiersContext, StatContext}
import io.github.effiban.scala2java.entities.JavaScope.JavaScope
import io.github.effiban.scala2java.entities.JavaTreeType
import io.github.effiban.scala2java.resolvers.JavaModifiersResolver
import io.github.effiban.scala2java.writers.JavaWriter

import scala.meta.Decl

trait DeclValTraverser {
  def traverse(valDecl: Decl.Val, context: StatContext = StatContext()): Unit
}

//TODO - if Java owner is an interface, the output should be an accessor method
private[traversers] class DeclValTraverserImpl(annotListTraverser: => AnnotListTraverser,
                                               typeTraverser: => TypeTraverser,
                                               patListTraverser: => PatListTraverser,
                                               javaModifiersResolver: JavaModifiersResolver)
                                              (implicit javaWriter: JavaWriter) extends DeclValTraverser {

  import javaWriter._

  //TODO replace interface data member declaration (invalid in Java) with method declaration
  override def traverse(valDecl: Decl.Val, context: StatContext = StatContext()): Unit = {
    annotListTraverser.traverseMods(valDecl.mods)
    writeModifiers(resolveJavaModifiers(valDecl, context.javaScope))
    typeTraverser.traverse(valDecl.decltpe)
    write(" ")
    //TODO - verify when not simple case
    patListTraverser.traverse(valDecl.pats)
  }

  private def resolveJavaModifiers(valDecl: Decl.Val, javaScope: JavaScope) = {
    val context = JavaModifiersContext(
      scalaTree = valDecl,
      scalaMods = valDecl.mods,
      javaTreeType = JavaTreeType.Variable,
      javaScope = javaScope
    )
    javaModifiersResolver.resolve(context)
  }
}