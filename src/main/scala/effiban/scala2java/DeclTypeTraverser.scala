package effiban.scala2java

import scala.meta.Decl

trait DeclTypeTraverser extends ScalaTreeTraverser[Decl.Type]

private[scala2java] class DeclTypeTraverserImpl(typeParamListTraverser: => TypeParamListTraverser,
                                                javaModifiersResolver: JavaModifiersResolver)
                                               (implicit javaEmitter: JavaEmitter) extends DeclTypeTraverser {

  import javaEmitter._

  // Scala type declaration : Closest thing in Java is an empty interface with same params
  override def traverse(typeDecl: Decl.Type): Unit = {
    emitLine()
    //TODO handle annotations
    emitTypeDeclaration(modifiers = javaModifiersResolver.resolveForInterface(typeDecl.mods),
      typeKeyword = "interface",
      name = typeDecl.name.toString)
    typeParamListTraverser.traverse(typeDecl.tparams)
    //TODO handle bounds properly
    emitBlockStart()
    emitBlockEnd()
  }
}

object DeclTypeTraverser extends DeclTypeTraverserImpl(TypeParamListTraverser, JavaModifiersResolver)
