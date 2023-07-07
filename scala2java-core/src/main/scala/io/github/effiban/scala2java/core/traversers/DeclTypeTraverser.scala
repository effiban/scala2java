package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{JavaTreeTypeContext, ModifiersContext, StatContext}
import io.github.effiban.scala2java.core.entities.JavaTreeTypeToKeywordMapping
import io.github.effiban.scala2java.core.renderers.contextfactories.ModifiersRenderContextFactory
import io.github.effiban.scala2java.core.renderers.{ModListRenderer, TypeParamListRenderer}
import io.github.effiban.scala2java.core.resolvers.JavaTreeTypeResolver
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Decl

trait DeclTypeTraverser {
  def traverse(typeDecl: Decl.Type, context: StatContext = StatContext()): Unit
}

private[traversers] class DeclTypeTraverserImpl(modListTraverser: => ModListTraverser,
                                                modifiersRenderContextFactory: ModifiersRenderContextFactory,
                                                modListRenderer: => ModListRenderer,
                                                typeParamTraverser: => TypeParamTraverser,
                                                typeParamListRenderer: => TypeParamListRenderer,
                                                javaTreeTypeResolver: JavaTreeTypeResolver)
                                               (implicit javaWriter: JavaWriter) extends DeclTypeTraverser {

  import javaWriter._

  // Scala type declaration : Closest thing in Java is an empty interface with same params
  override def traverse(typeDecl: Decl.Type, context: StatContext = StatContext()): Unit = {
    writeLine()
    //TODO - transform to Defn.Trait instead of traversing directly (+ the Java tree type is incorrect anyway)
    val javaTreeType = javaTreeTypeResolver.resolve(JavaTreeTypeContext(typeDecl, typeDecl.mods))
    val modListTraversalResult = modListTraverser.traverse(ModifiersContext(typeDecl, javaTreeType, context.javaScope))
    val modifiersRenderContext = modifiersRenderContextFactory(modListTraversalResult)
    modListRenderer.render(modifiersRenderContext)
    writeNamedType(JavaTreeTypeToKeywordMapping(javaTreeType), typeDecl.name.value)
    val traversedTypeParams = typeDecl.tparams.map(typeParamTraverser.traverse)
    typeParamListRenderer.render(traversedTypeParams)
    //TODO handle bounds properly
    writeBlockStart()
    writeBlockEnd()
  }
}
