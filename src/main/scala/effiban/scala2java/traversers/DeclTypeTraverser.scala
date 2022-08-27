package effiban.scala2java.traversers

import effiban.scala2java.entities.JavaTreeType
import effiban.scala2java.entities.TraversalContext.javaScope
import effiban.scala2java.resolvers.{JavaModifiersResolver, JavaModifiersResolverParams}
import effiban.scala2java.writers.JavaWriter

import scala.meta.Decl

trait DeclTypeTraverser extends ScalaTreeTraverser[Decl.Type]

private[traversers] class DeclTypeTraverserImpl(typeParamListTraverser: => TypeParamListTraverser,
                                                javaModifiersResolver: JavaModifiersResolver)
                                               (implicit javaWriter: JavaWriter) extends DeclTypeTraverser {

  import javaWriter._

  // Scala type declaration : Closest thing in Java is an empty interface with same params
  override def traverse(typeDecl: Decl.Type): Unit = {
    writeLine()
    //TODO handle annotations
    writeTypeDeclaration(modifiers = resolveJavaModifiers(typeDecl),
      typeKeyword = "interface",
      name = typeDecl.name.toString)
    typeParamListTraverser.traverse(typeDecl.tparams)
    //TODO handle bounds properly
    writeBlockStart()
    writeBlockEnd()
  }

  private def resolveJavaModifiers(typeDecl: Decl.Type) = {
    val params = JavaModifiersResolverParams(
      scalaTree = typeDecl,
      scalaMods = typeDecl.mods,
      javaTreeType = JavaTreeType.Interface,
      javaScope = javaScope
    )
    javaModifiersResolver.resolve(params)
  }
}
