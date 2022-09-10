package effiban.scala2java.traversers

import effiban.scala2java.contexts.{JavaModifiersContext, JavaTreeTypeContext}
import effiban.scala2java.entities.JavaTreeType.JavaTreeType
import effiban.scala2java.entities.JavaTreeTypeToKeywordMapping
import effiban.scala2java.entities.TraversalContext.javaScope
import effiban.scala2java.resolvers.{JavaModifiersResolver, JavaTreeTypeResolver}
import effiban.scala2java.writers.JavaWriter

import scala.meta.Decl

trait DeclTypeTraverser extends ScalaTreeTraverser[Decl.Type]

private[traversers] class DeclTypeTraverserImpl(typeParamListTraverser: => TypeParamListTraverser,
                                                javaModifiersResolver: JavaModifiersResolver,
                                                javaTreeTypeResolver: JavaTreeTypeResolver)
                                               (implicit javaWriter: JavaWriter) extends DeclTypeTraverser {

  import javaWriter._

  // Scala type declaration : Closest thing in Java is an empty interface with same params
  override def traverse(typeDecl: Decl.Type): Unit = {
    writeLine()
    //TODO handle annotations
    val javaTreeType = javaTreeTypeResolver.resolve(JavaTreeTypeContext(typeDecl, typeDecl.mods))
    writeTypeDeclaration(modifiers = resolveJavaModifiers(typeDecl, javaTreeType),
      typeKeyword = JavaTreeTypeToKeywordMapping(javaTreeType),
      name = typeDecl.name.toString)
    typeParamListTraverser.traverse(typeDecl.tparams)
    //TODO handle bounds properly
    writeBlockStart()
    writeBlockEnd()
  }

  private def resolveJavaModifiers(typeDecl: Decl.Type, javaTreeType: JavaTreeType) = {
    val context = JavaModifiersContext(
      scalaTree = typeDecl,
      scalaMods = typeDecl.mods,
      javaTreeType = javaTreeType,
      javaScope = javaScope
    )
    javaModifiersResolver.resolve(context)
  }
}
