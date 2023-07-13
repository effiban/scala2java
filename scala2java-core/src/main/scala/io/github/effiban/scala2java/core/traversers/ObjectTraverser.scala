package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts._
import io.github.effiban.scala2java.core.entities.JavaTreeTypeToKeywordMapping
import io.github.effiban.scala2java.core.renderers.ModListRenderer
import io.github.effiban.scala2java.core.renderers.contextfactories.ModifiersRenderContextFactory
import io.github.effiban.scala2java.core.resolvers.{JavaChildScopeResolver, JavaTreeTypeResolver}
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Defn

trait ObjectTraverser {
  def traverse(objectDef: Defn.Object, context: StatContext = StatContext()): Unit
}

private[traversers] class ObjectTraverserImpl(statModListTraverser: => StatModListTraverser,
                                              modifiersRenderContextFactory: ModifiersRenderContextFactory,
                                              modListRenderer: => ModListRenderer,
                                              templateTraverser: => TemplateTraverser,
                                              javaTreeTypeResolver: JavaTreeTypeResolver,
                                              javaChildScopeResolver: JavaChildScopeResolver)
                                             (implicit javaWriter: JavaWriter) extends ObjectTraverser {

  import javaWriter._

  override def traverse(objectDef: Defn.Object, context: StatContext = StatContext()): Unit = {
    writeLine()
    val javaTreeType = javaTreeTypeResolver.resolve(JavaTreeTypeContext(objectDef, objectDef.mods))
    val modListTraversalResult = statModListTraverser.traverse(ModifiersContext(objectDef, javaTreeType, context.javaScope))
    val modifiersRenderContext = modifiersRenderContextFactory(modListTraversalResult)
    modListRenderer.render(modifiersRenderContext)
    writeNamedType(JavaTreeTypeToKeywordMapping(javaTreeType), objectDef.name.value)
    val javaChildScope = javaChildScopeResolver.resolve(JavaChildScopeContext(objectDef, javaTreeType))
    // TODO if child scope is utility class, add private ctor.
    templateTraverser.traverse(objectDef.templ, TemplateContext(javaScope = javaChildScope))
  }
}

