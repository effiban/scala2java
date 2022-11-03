package io.github.effiban.scala2java.traversers

import io.github.effiban.scala2java.contexts._
import io.github.effiban.scala2java.entities.JavaScope.JavaScope
import io.github.effiban.scala2java.entities.JavaTreeType.JavaTreeType
import io.github.effiban.scala2java.entities.JavaTreeTypeToKeywordMapping
import io.github.effiban.scala2java.resolvers.{JavaChildScopeResolver, JavaTreeTypeResolver}
import io.github.effiban.scala2java.writers.JavaWriter

import scala.meta.Defn

trait ObjectTraverser {
  def traverse(objectDef: Defn.Object, context: StatContext = StatContext()): Unit
}

private[traversers] class ObjectTraverserImpl(modListTraverser: => ModListTraverser,
                                              templateTraverser: => TemplateTraverser,
                                              javaTreeTypeResolver: JavaTreeTypeResolver,
                                              javaChildScopeResolver: JavaChildScopeResolver)
                                             (implicit javaWriter: JavaWriter) extends ObjectTraverser {

  import javaWriter._

  override def traverse(objectDef: Defn.Object, context: StatContext = StatContext()): Unit = {
    writeLine()
    val javaTreeType = javaTreeTypeResolver.resolve(JavaTreeTypeContext(objectDef, objectDef.mods))
    modListTraverser.traverse(toJavaModifiersContext(objectDef, javaTreeType, context.javaScope))
    writeNamedType(JavaTreeTypeToKeywordMapping(javaTreeType), objectDef.name.value)
    val javaChildScope = javaChildScopeResolver.resolve(JavaChildScopeContext(objectDef, javaTreeType))
    // TODO if child scope is utility class, add private ctor.
    templateTraverser.traverse(objectDef.templ, TemplateContext(javaScope = javaChildScope))
  }

  private def toJavaModifiersContext(objectDef: Defn.Object,
                                     javaTreeType: JavaTreeType,
                                     javaScope: JavaScope) =
    JavaModifiersContext(
      scalaTree = objectDef,
      scalaMods = objectDef.mods,
      javaTreeType = javaTreeType,
      javaScope = javaScope
    )
}

