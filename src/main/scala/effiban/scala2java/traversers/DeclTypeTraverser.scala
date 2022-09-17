package effiban.scala2java.traversers

import effiban.scala2java.contexts.{JavaModifiersContext, JavaTreeTypeContext, StatContext}
import effiban.scala2java.entities.JavaTreeType.JavaTreeType
import effiban.scala2java.entities.JavaTreeTypeToKeywordMapping
import effiban.scala2java.resolvers.{JavaModifiersResolver, JavaTreeTypeResolver}
import effiban.scala2java.writers.JavaWriter

import scala.meta.Decl

trait DeclTypeTraverser {
  def traverse(typeDecl: Decl.Type, context: StatContext = StatContext()): Unit
}

private[traversers] class DeclTypeTraverserImpl(typeParamListTraverser: => TypeParamListTraverser,
                                                javaModifiersResolver: JavaModifiersResolver,
                                                javaTreeTypeResolver: JavaTreeTypeResolver)
                                               (implicit javaWriter: JavaWriter) extends DeclTypeTraverser {

  import javaWriter._

  // Scala type declaration : Closest thing in Java is an empty interface with same params
  override def traverse(typeDecl: Decl.Type, context: StatContext = StatContext()): Unit = {
    writeLine()
    //TODO handle annotations
    val javaTreeType = javaTreeTypeResolver.resolve(JavaTreeTypeContext(typeDecl, typeDecl.mods))
    writeTypeDeclaration(modifiers = resolveJavaModifiers(typeDecl, javaTreeType, context.javaScope),
      typeKeyword = JavaTreeTypeToKeywordMapping(javaTreeType),
      name = typeDecl.name.toString)
    typeParamListTraverser.traverse(typeDecl.tparams)
    //TODO handle bounds properly
    writeBlockStart()
    writeBlockEnd()
  }

  private def resolveJavaModifiers(typeDecl: Decl.Type,
                                   javaTreeType: JavaTreeType,
                                   parentJavaScope: JavaTreeType) = {
    val javaModifiersContext = JavaModifiersContext(
      scalaTree = typeDecl,
      scalaMods = typeDecl.mods,
      javaTreeType = javaTreeType,
      javaScope = parentJavaScope
    )
    javaModifiersResolver.resolve(javaModifiersContext)
  }
}
