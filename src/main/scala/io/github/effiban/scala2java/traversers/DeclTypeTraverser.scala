package io.github.effiban.scala2java.traversers

import io.github.effiban.scala2java.contexts.{JavaModifiersContext, JavaTreeTypeContext, StatContext}
import io.github.effiban.scala2java.entities.JavaScope.JavaScope
import io.github.effiban.scala2java.entities.JavaTreeType.JavaTreeType
import io.github.effiban.scala2java.entities.JavaTreeTypeToKeywordMapping
import io.github.effiban.scala2java.resolvers.JavaTreeTypeResolver
import io.github.effiban.scala2java.writers.JavaWriter

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
    modListTraverser.traverse(toJavaModifiersContext(typeDecl, javaTreeType, context.javaScope))
    writeNamedType(JavaTreeTypeToKeywordMapping(javaTreeType), typeDecl.name.value)
    typeParamListTraverser.traverse(typeDecl.tparams)
    //TODO handle bounds properly
    writeBlockStart()
    writeBlockEnd()
  }

  private def toJavaModifiersContext(typeDecl: Decl.Type,
                                     javaTreeType: JavaTreeType,
                                     javaScope: JavaScope) =
    JavaModifiersContext(
      scalaTree = typeDecl,
      scalaMods = typeDecl.mods,
      javaTreeType = javaTreeType,
      javaScope = javaScope
    )
}
