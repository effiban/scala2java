package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts._
import io.github.effiban.scala2java.core.resolvers.{JavaChildScopeResolver, JavaTreeTypeResolver}

import scala.meta.Defn

trait ObjectTraverser {
  def traverse(objectDef: Defn.Object, context: StatContext = StatContext()): Defn.Object
}

private[traversers] class ObjectTraverserImpl(statModListTraverser: => StatModListTraverser,
                                              templateTraverser: => TemplateTraverser,
                                              javaTreeTypeResolver: JavaTreeTypeResolver,
                                              javaChildScopeResolver: JavaChildScopeResolver) extends ObjectTraverser {

  override def traverse(objectDef: Defn.Object, context: StatContext = StatContext()): Defn.Object = {
    val javaTreeType = javaTreeTypeResolver.resolve(JavaTreeTypeContext(objectDef, objectDef.mods))
    val traversedMods = statModListTraverser.traverse(objectDef.mods)
    val javaChildScope = javaChildScopeResolver.resolve(JavaChildScopeContext(objectDef, javaTreeType))
    // TODO if child scope is utility class, add private ctor.
    val templateTraversalResult = templateTraverser.traverse(objectDef.templ, TemplateContext(javaScope = javaChildScope))

    Defn.Object(
      mods = traversedMods,
      name = objectDef.name,
      templ = templateTraversalResult.template
    )
  }
}

