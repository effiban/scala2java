package io.github.effiban.scala2java.traversers

import io.github.effiban.scala2java.contexts.{JavaModifiersContext, JavaTreeTypeContext, StatContext}
import io.github.effiban.scala2java.entities.JavaScope.JavaScope
import io.github.effiban.scala2java.entities.JavaTreeType.JavaTreeType
import io.github.effiban.scala2java.entities.JavaTreeTypeToKeywordMapping
import io.github.effiban.scala2java.resolvers.JavaTreeTypeResolver
import io.github.effiban.scala2java.writers.JavaWriter

import scala.meta.Type.Bounds
import scala.meta.{Defn, Type}

trait DefnTypeTraverser {
  def traverse(typeDef: Defn.Type, context: StatContext = StatContext()): Unit
}

private[traversers] class DefnTypeTraverserImpl(modListTraverser: => ModListTraverser,
                                                typeParamListTraverser: => TypeParamListTraverser,
                                                typeTraverser: => TypeTraverser,
                                                typeBoundsTraverser: => TypeBoundsTraverser,
                                                javaTreeTypeResolver: JavaTreeTypeResolver)
                                               (implicit javaWriter: JavaWriter) extends DefnTypeTraverser {

  import javaWriter._

  // Scala type definition : Can sometimes be replaced by an empty interface
  override def traverse(typeDef: Defn.Type, context: StatContext = StatContext()): Unit = {
    //TODO - transform to Defn.Trait instead of traversing directly (+ the Java tree type is incorrect anyway)
    val javaTreeType = javaTreeTypeResolver.resolve(JavaTreeTypeContext(typeDef, typeDef.mods))
    modListTraverser.traverse(toJavaModifiersContext(typeDef, javaTreeType, context.javaScope))
    writeNamedType(JavaTreeTypeToKeywordMapping(javaTreeType), typeDef.name.value)
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

  private def toJavaModifiersContext(typeDefn: Defn.Type,
                                     javaTreeType: JavaTreeType,
                                     javaScope: JavaScope) =
    JavaModifiersContext(
      scalaTree = typeDefn,
      scalaMods = typeDefn.mods,
      javaTreeType = javaTreeType,
      javaScope = javaScope
    )
}
