package io.github.effiban.scala2java.traversers

import io.github.effiban.scala2java.contexts._
import io.github.effiban.scala2java.entities.JavaScope.JavaScope
import io.github.effiban.scala2java.entities.JavaTreeType.JavaTreeType
import io.github.effiban.scala2java.entities.JavaTreeTypeToKeywordMapping
import io.github.effiban.scala2java.resolvers.{JavaChildScopeResolver, JavaModifiersResolver, JavaTreeTypeResolver}
import io.github.effiban.scala2java.writers.JavaWriter

import scala.meta.Defn.Trait

trait TraitTraverser {
  def traverse(traitDef: Trait, context: ClassOrTraitContext = ClassOrTraitContext()): Unit
}

private[traversers] class TraitTraverserImpl(annotListTraverser: => AnnotListTraverser,
                                             typeParamListTraverser: => TypeParamListTraverser,
                                             templateTraverser: => TemplateTraverser,
                                             javaModifiersResolver: JavaModifiersResolver,
                                             javaTreeTypeResolver: JavaTreeTypeResolver,
                                             javaChildScopeResolver: JavaChildScopeResolver)
                                            (implicit javaWriter: JavaWriter) extends TraitTraverser {

  import javaWriter._

  override def traverse(traitDef: Trait, context: ClassOrTraitContext = ClassOrTraitContext()): Unit = {
    writeLine()
    annotListTraverser.traverseMods(traitDef.mods)
    val javaTreeType = javaTreeTypeResolver.resolve(JavaTreeTypeContext(traitDef, traitDef.mods))
    writeTypeDeclaration(modifiers = resolveJavaModifiers(traitDef, javaTreeType, context.javaScope),
      typeKeyword = JavaTreeTypeToKeywordMapping(javaTreeType),
      name = traitDef.name.toString)
    typeParamListTraverser.traverse(traitDef.tparams)
    val javaChildScope = javaChildScopeResolver.resolve(JavaChildScopeContext(traitDef, javaTreeType))
    val templateContext = TemplateContext(javaScope = javaChildScope, permittedSubTypeNames = context.permittedSubTypeNames)
    templateTraverser.traverse(traitDef.templ, templateContext)
  }

  private def resolveJavaModifiers(traitDef: Trait,
                                   javaTreeType: JavaTreeType,
                                   parentJavaScope: JavaScope) = {
    val javaModifiersContext = JavaModifiersContext(
      scalaTree = traitDef,
      scalaMods = traitDef.mods,
      javaTreeType = javaTreeType,
      javaScope = parentJavaScope
    )
    javaModifiersResolver.resolve(javaModifiersContext)
  }
}
