package effiban.scala2java.traversers

import effiban.scala2java.entities.JavaTreeType
import effiban.scala2java.entities.TraversalContext.javaScope
import effiban.scala2java.resolvers.{JavaModifiersResolver, JavaModifiersResolverParams}
import effiban.scala2java.writers.JavaWriter

import scala.meta.{Defn, Type}

trait DefnTypeTraverser extends ScalaTreeTraverser[Defn.Type]

private[traversers] class DefnTypeTraverserImpl(typeParamListTraverser: => TypeParamListTraverser,
                                                typeTraverser: => TypeTraverser,
                                                javaModifiersResolver: JavaModifiersResolver)
                                               (implicit javaWriter: JavaWriter) extends DefnTypeTraverser {

  import javaWriter._

  // Scala type definition : Can sometimes be replaced by an empty interface
  override def traverse(typeDef: Defn.Type): Unit = {
    writeTypeDeclaration(modifiers = resolveJavaModifiers(typeDef),
      typeKeyword = "interface",
      name = typeDef.name.toString)
    typeParamListTraverser.traverse(typeDef.tparams)
    // Only an upper bound can be supported by extending in Java
    (typeDef.bounds.lo, typeDef.bounds.hi) match {
      case (Some(lo), None) => writeComment(s"super $lo")
      case (None, Some(hi)) =>
        write(" extends ")
        typeTraverser.traverse(hi)
      case (None, None) =>
      case _ => writeComment(typeDef.bounds.toString)
    }
    // If the body type exists, extend it in Java
    typeDef.body match {
      case _: Type.AnonymousName =>
      case rhsType =>
        write(" extends ")
        typeTraverser.traverse(rhsType)
    }
    writeBlockStart()
    writeBlockEnd()
  }

  private def resolveJavaModifiers(typeDef: Defn.Type) = {
    val params = JavaModifiersResolverParams(
      scalaTree = typeDef,
      scalaMods = typeDef.mods,
      javaTreeType = JavaTreeType.Interface,
      javaScope = javaScope
    )
    javaModifiersResolver.resolve(params)
  }
}
