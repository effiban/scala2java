package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{JavaTreeTypeContext, ModifiersContext, StatContext}
import io.github.effiban.scala2java.core.entities.JavaTreeTypeToKeywordMapping
import io.github.effiban.scala2java.core.resolvers.JavaTreeTypeResolver
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Decl

trait DeclTypeTraverser {
  def traverse(typeDecl: Decl.Type, context: StatContext = StatContext()): Unit
}

private[traversers] class DeclTypeTraverserImpl(modListTraverser: => ModListTraverser,
                                                typeParamListTraverser: => TypeParamListTraverser,
                                                javaTreeTypeResolver: JavaTreeTypeResolver)
                                               (implicit javaWriter: JavaWriter) extends DeclTypeTraverser {

  import javaWriter._

  // Scala type declaration : Closest thing in Java is an empty interface with same params
  override def traverse(typeDecl: Decl.Type, context: StatContext = StatContext()): Unit = {
    writeLine()
    //TODO - transform to Defn.Trait instead of traversing directly (+ the Java tree type is incorrect anyway)
    val javaTreeType = javaTreeTypeResolver.resolve(JavaTreeTypeContext(typeDecl, typeDecl.mods))
    modListTraverser.traverse(ModifiersContext(typeDecl, javaTreeType, context.javaScope))
    writeNamedType(JavaTreeTypeToKeywordMapping(javaTreeType), typeDecl.name.value)
    typeParamListTraverser.traverse(typeDecl.tparams)
    //TODO handle bounds properly
    writeBlockStart()
    writeBlockEnd()
  }
}