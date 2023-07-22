package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts._
import io.github.effiban.scala2java.core.entities.JavaTreeTypeToKeywordMapping
import io.github.effiban.scala2java.core.resolvers.{JavaChildScopeResolver, JavaTreeTypeResolver}
import io.github.effiban.scala2java.core.traversers.results.ObjectTraversalResult

import scala.meta.Defn

trait ObjectTraverser {
  def traverse(objectDef: Defn.Object, context: StatContext = StatContext()): ObjectTraversalResult
}

private[traversers] class ObjectTraverserImpl(statModListTraverser: => StatModListTraverser,
                                              templateTraverser: => TemplateTraverser,
                                              javaTreeTypeResolver: JavaTreeTypeResolver,
                                              javaChildScopeResolver: JavaChildScopeResolver) extends ObjectTraverser {

  override def traverse(objectDef: Defn.Object, context: StatContext = StatContext()): ObjectTraversalResult = {
    val javaTreeType = javaTreeTypeResolver.resolve(JavaTreeTypeContext(objectDef, objectDef.mods))
    val modListTraversalResult = statModListTraverser.traverse(ModifiersContext(objectDef, javaTreeType, context.javaScope))
    val javaTypeKeyword = JavaTreeTypeToKeywordMapping(javaTreeType)
    val javaChildScope = javaChildScopeResolver.resolve(JavaChildScopeContext(objectDef, javaTreeType))
    // TODO if child scope is utility class, add private ctor.
    val templateTraversalResult = templateTraverser.traverse(objectDef.templ, TemplateContext(javaScope = javaChildScope))

    ObjectTraversalResult(
      scalaMods = modListTraversalResult.scalaMods,
      javaModifiers = modListTraversalResult.javaModifiers,
      javaTypeKeyword = javaTypeKeyword,
      name = objectDef.name,
      maybeInheritanceKeyword = templateTraversalResult.maybeInheritanceKeyword,
      inits = templateTraversalResult.inits,
      self = templateTraversalResult.self,
      statResults = templateTraversalResult.statResults
    )

  }
}

