package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts._
import io.github.effiban.scala2java.core.entities.JavaTreeTypeToKeywordMapping
import io.github.effiban.scala2java.core.renderers.contextfactories.ModifiersRenderContextFactory
import io.github.effiban.scala2java.core.renderers.{ModListRenderer, TypeParamListRenderer}
import io.github.effiban.scala2java.core.resolvers.{JavaChildScopeResolver, JavaTreeTypeResolver}
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Defn.Trait

@deprecated
trait DeprecatedTraitTraverser {
  def traverse(traitDef: Trait, context: ClassOrTraitContext = ClassOrTraitContext()): Unit
}

@deprecated
private[traversers] class DeprecatedTraitTraverserImpl(statModListTraverser: => StatModListTraverser,
                                                       modifiersRenderContextFactory: ModifiersRenderContextFactory,
                                                       modListRenderer: => ModListRenderer,
                                                       typeParamTraverser: => TypeParamTraverser,
                                                       typeParamListRenderer: => TypeParamListRenderer,
                                                       templateTraverser: => DeprecatedTemplateTraverser,
                                                       javaTreeTypeResolver: JavaTreeTypeResolver,
                                                       javaChildScopeResolver: JavaChildScopeResolver)
                                                      (implicit javaWriter: JavaWriter) extends DeprecatedTraitTraverser {

  import javaWriter._

  override def traverse(traitDef: Trait, context: ClassOrTraitContext = ClassOrTraitContext()): Unit = {
    writeLine()
    val javaTreeType = javaTreeTypeResolver.resolve(JavaTreeTypeContext(traitDef, traitDef.mods))
    val modListTraversalResult = statModListTraverser.traverse(ModifiersContext(traitDef, javaTreeType, context.javaScope))
    val modifiersRenderContext = modifiersRenderContextFactory(modListTraversalResult)
    modListRenderer.render(modifiersRenderContext)
    writeNamedType(JavaTreeTypeToKeywordMapping(javaTreeType), traitDef.name.value)
    val traversedTypeParams = traitDef.tparams.map(typeParamTraverser.traverse)
    typeParamListRenderer.render(traversedTypeParams)
    val javaChildScope = javaChildScopeResolver.resolve(JavaChildScopeContext(traitDef, javaTreeType))
    val templateContext = TemplateContext(javaScope = javaChildScope, permittedSubTypeNames = context.permittedSubTypeNames)
    templateTraverser.traverse(traitDef.templ, templateContext)
  }
}
