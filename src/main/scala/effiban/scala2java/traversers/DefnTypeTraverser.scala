package effiban.scala2java.traversers

import effiban.scala2java.contexts.{JavaModifiersContext, JavaTreeTypeContext, StatContext}
import effiban.scala2java.entities.JavaScope.JavaScope
import effiban.scala2java.entities.JavaTreeType.JavaTreeType
import effiban.scala2java.entities.JavaTreeTypeToKeywordMapping
import effiban.scala2java.resolvers.{JavaModifiersResolver, JavaTreeTypeResolver}
import effiban.scala2java.writers.JavaWriter

import scala.meta.Type.Bounds
import scala.meta.{Defn, Type}

trait DefnTypeTraverser {
  def traverse(typeDef: Defn.Type, context: StatContext = StatContext()): Unit
}

private[traversers] class DefnTypeTraverserImpl(typeParamListTraverser: => TypeParamListTraverser,
                                                typeTraverser: => TypeTraverser,
                                                typeBoundsTraverser: => TypeBoundsTraverser,
                                                javaModifiersResolver: JavaModifiersResolver,
                                                javaTreeTypeResolver: JavaTreeTypeResolver)
                                               (implicit javaWriter: JavaWriter) extends DefnTypeTraverser {

  import javaWriter._

  // Scala type definition : Can sometimes be replaced by an empty interface
  override def traverse(typeDef: Defn.Type, context: StatContext = StatContext()): Unit = {
    //TODO - handle annotations
    val javaTreeType = javaTreeTypeResolver.resolve(JavaTreeTypeContext(typeDef, typeDef.mods))
    writeTypeDeclaration(modifiers = resolveJavaModifiers(typeDef, javaTreeType, context.javaScope),
      typeKeyword = JavaTreeTypeToKeywordMapping(javaTreeType),
      name = typeDef.name.toString)
    typeParamListTraverser.traverse(typeDef.tparams)
    typeDef.bounds match {
      case Bounds(None, None) =>
      case bounds =>
        write(" ")
        typeBoundsTraverser.traverse(bounds)
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

  private def resolveJavaModifiers(typeDef: Defn.Type,
                                   javaTreeType: JavaTreeType,
                                   parentJavaScope: JavaScope) = {
    val javaModifiersContext = JavaModifiersContext(
      scalaTree = typeDef,
      scalaMods = typeDef.mods,
      javaTreeType = javaTreeType,
      javaScope = parentJavaScope
    )
    javaModifiersResolver.resolve(javaModifiersContext)
  }
}
