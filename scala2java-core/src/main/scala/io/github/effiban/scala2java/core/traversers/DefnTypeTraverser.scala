package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{JavaTreeTypeContext, ModifiersContext, StatContext}
import io.github.effiban.scala2java.core.entities.JavaTreeTypeToKeywordMapping
import io.github.effiban.scala2java.core.renderers.contextfactories.ModifiersRenderContextFactory
import io.github.effiban.scala2java.core.renderers.{ModListRenderer, TypeBoundsRenderer, TypeRenderer}
import io.github.effiban.scala2java.core.resolvers.JavaTreeTypeResolver
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Type.Bounds
import scala.meta.{Defn, Type}

trait DefnTypeTraverser {
  def traverse(typeDef: Defn.Type, context: StatContext = StatContext()): Unit
}

private[traversers] class DefnTypeTraverserImpl(modListTraverser: => ModListTraverser,
                                                modifiersRenderContextFactory: ModifiersRenderContextFactory,
                                                modListRenderer: => ModListRenderer,
                                                typeParamListTraverser: => TypeParamListTraverser,
                                                typeTraverser: => TypeTraverser,
                                                typeRenderer: => TypeRenderer,
                                                typeBoundsTraverser: => TypeBoundsTraverser,
                                                typeBoundsRenderer: => TypeBoundsRenderer,
                                                javaTreeTypeResolver: JavaTreeTypeResolver)
                                               (implicit javaWriter: JavaWriter) extends DefnTypeTraverser {

  import javaWriter._

  // Scala type definition : Can sometimes be replaced by an empty interface
  override def traverse(typeDef: Defn.Type, context: StatContext = StatContext()): Unit = {
    //TODO - transform to Defn.Trait instead of traversing directly (+ the Java tree type is incorrect anyway)
    val javaTreeType = javaTreeTypeResolver.resolve(JavaTreeTypeContext(typeDef, typeDef.mods))
    val modListTraversalResult = modListTraverser.traverse(ModifiersContext(typeDef, javaTreeType, context.javaScope))
    val modifiersRenderContext = modifiersRenderContextFactory(modListTraversalResult)
    modListRenderer.render(modifiersRenderContext)
    writeNamedType(JavaTreeTypeToKeywordMapping(javaTreeType), typeDef.name.value)
    typeParamListTraverser.traverse(typeDef.tparams)
    typeDef.bounds match {
      case Bounds(None, None) =>
      case bounds =>
        write(" ")
        val traversedTypeBounds = typeBoundsTraverser.traverse(bounds)
        typeBoundsRenderer.render(traversedTypeBounds)
    }
    // If the body type exists, extend it in Java
    typeDef.body match {
      case _: Type.AnonymousName =>
      case rhsType =>
        write(" extends ")
        val traversedType = typeTraverser.traverse(rhsType)
        typeRenderer.render(traversedType)
    }
    writeBlockStart()
    writeBlockEnd()
  }
}
