package io.github.effiban.scala2java.traversers

import io.github.effiban.scala2java.contexts.{JavaModifiersContext, StatContext}
import io.github.effiban.scala2java.entities.JavaScope.JavaScope
import io.github.effiban.scala2java.entities.JavaTreeType
import io.github.effiban.scala2java.resolvers.JavaModifiersResolver
import io.github.effiban.scala2java.writers.JavaWriter

import scala.meta.Decl

trait DeclVarTraverser {
  def traverse(varDecl: Decl.Var, context: StatContext = StatContext()): Unit
}

//TODO - if Java owner is an interface, the output should be a pair of accessor/mutator methods
private[traversers] class DeclVarTraverserImpl(annotListTraverser: => AnnotListTraverser,
                                               typeTraverser: => TypeTraverser,
                                               patListTraverser: => PatListTraverser,
                                               javaModifiersResolver: JavaModifiersResolver)
                                              (implicit javaWriter: JavaWriter) extends DeclVarTraverser {

  import javaWriter._

  //TODO replace mutable interface data member (invalid in Java) with method
  override def traverse(varDecl: Decl.Var, context: StatContext = StatContext()): Unit = {
    annotListTraverser.traverseMods(varDecl.mods)
    writeModifiers(resolveJavaModifiers(varDecl, context.javaScope))
    typeTraverser.traverse(varDecl.decltpe)
    write(" ")
    //TODO - verify when not simple case
    patListTraverser.traverse(varDecl.pats)
  }

  private def resolveJavaModifiers(varDecl: Decl.Var, parentJavaScope: JavaScope) = {
    val javaModifiersContext = JavaModifiersContext(
      scalaTree = varDecl,
      scalaMods = varDecl.mods,
      javaTreeType = JavaTreeType.Variable,
      javaScope = parentJavaScope
    )
    javaModifiersResolver.resolve(javaModifiersContext)
  }
}
