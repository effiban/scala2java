package io.github.effiban.scala2java.traversers

import io.github.effiban.scala2java.contexts._
import io.github.effiban.scala2java.entities.JavaScope.JavaScope
import io.github.effiban.scala2java.entities.JavaTreeType.JavaTreeType
import io.github.effiban.scala2java.entities.JavaTreeTypeToKeywordMapping
import io.github.effiban.scala2java.resolvers.{JavaChildScopeResolver, JavaTreeTypeResolver}
import io.github.effiban.scala2java.writers.JavaWriter

import scala.meta.Defn.Trait

trait TraitTraverser {
  def traverse(traitDef: Trait, context: ClassOrTraitContext = ClassOrTraitContext()): Unit
}

private[traversers] class TraitTraverserImpl(modListTraverser: => ModListTraverser,
                                             typeParamListTraverser: => TypeParamListTraverser,
                                             templateTraverser: => TemplateTraverser,
                                             javaTreeTypeResolver: JavaTreeTypeResolver,
                                             javaChildScopeResolver: JavaChildScopeResolver)
                                            (implicit javaWriter: JavaWriter) extends TraitTraverser {

  import javaWriter._

  override def traverse(traitDef: Trait, context: ClassOrTraitContext = ClassOrTraitContext()): Unit = {
    writeLine()
    val javaTreeType = javaTreeTypeResolver.resolve(JavaTreeTypeContext(traitDef, traitDef.mods))
    modListTraverser.traverse(toJavaModifiersContext(traitDef, javaTreeType, context.javaScope))
    writeNamedType(JavaTreeTypeToKeywordMapping(javaTreeType), traitDef.name.value)
    typeParamListTraverser.traverse(traitDef.tparams)
    val javaChildScope = javaChildScopeResolver.resolve(JavaChildScopeContext(traitDef, javaTreeType))
    val templateContext = TemplateContext(javaScope = javaChildScope, permittedSubTypeNames = context.permittedSubTypeNames)
    templateTraverser.traverse(traitDef.templ, templateContext)
  }

  private def toJavaModifiersContext(traitDef: Trait,
                                     javaTreeType: JavaTreeType,
                                     javaScope: JavaScope) = {
    JavaModifiersContext(
      scalaTree = traitDef,
      scalaMods = traitDef.mods,
      javaTreeType = javaTreeType,
      javaScope = javaScope
    )
  }
}
