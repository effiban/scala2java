package effiban.scala2java.traversers

import effiban.scala2java.JavaEmitter
import effiban.scala2java.resolvers.JavaModifiersResolver

import scala.meta.{Defn, Type}

trait DefnTypeTraverser extends ScalaTreeTraverser[Defn.Type]

private[scala2java] class DefnTypeTraverserImpl(typeParamListTraverser: => TypeParamListTraverser,
                                                typeTraverser: => TypeTraverser,
                                                javaModifiersResolver: JavaModifiersResolver)
                                               (implicit javaEmitter: JavaEmitter) extends DefnTypeTraverser {

  import javaEmitter._

  // Scala type definition : Can sometimes be replaced by an empty interface
  override def traverse(typeDef: Defn.Type): Unit = {
    emitTypeDeclaration(modifiers = javaModifiersResolver.resolveForInterface(typeDef.mods),
      typeKeyword = "interface",
      name = typeDef.name.toString)
    typeParamListTraverser.traverse(typeDef.tparams)
    // Only an upper bound can be supported by extending in Java
    (typeDef.bounds.lo, typeDef.bounds.hi) match {
      case (Some(lo), None) => emitComment(s"super $lo")
      case (None, Some(hi)) =>
        emit(" extends ")
        typeTraverser.traverse(hi)
      case (None, None) =>
      case _ => emitComment(typeDef.bounds.toString)
    }
    // If the body type exists, extend it in Java
    typeDef.body match {
      case _: Type.AnonymousName =>
      case rhsType =>
        emit(" extends ")
        typeTraverser.traverse(rhsType)
    }
    emitBlockStart()
    emitBlockEnd()
  }
}

object DefnTypeTraverser extends DefnTypeTraverserImpl(
  TypeParamListTraverser,
  TypeTraverser,
  JavaModifiersResolver)
