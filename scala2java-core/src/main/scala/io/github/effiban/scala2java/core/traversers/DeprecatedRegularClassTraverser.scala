package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts._
import io.github.effiban.scala2java.core.entities.JavaTreeType.JavaTreeType
import io.github.effiban.scala2java.core.entities.JavaTreeTypeToKeywordMapping
import io.github.effiban.scala2java.core.renderers.contextfactories.ModifiersRenderContextFactory
import io.github.effiban.scala2java.core.renderers.{ModListRenderer, TypeParamListRenderer}
import io.github.effiban.scala2java.core.resolvers.{JavaChildScopeResolver, JavaTreeTypeResolver}
import io.github.effiban.scala2java.core.transformers.ParamToDeclVarTransformer
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Defn

@deprecated
trait DeprecatedRegularClassTraverser {
  def traverse(classDef: Defn.Class, context: ClassOrTraitContext = ClassOrTraitContext()): Unit
}

@deprecated
private[traversers] class DeprecatedRegularClassTraverserImpl(statModListTraverser: => StatModListTraverser,
                                                              modifiersRenderContextFactory: ModifiersRenderContextFactory,
                                                              modListRenderer: => ModListRenderer,
                                                              typeParamTraverser: => TypeParamTraverser,
                                                              typeParamListRenderer: => TypeParamListRenderer,
                                                              templateTraverser: => DeprecatedTemplateTraverser,
                                                              paramToDeclVarTransformer: ParamToDeclVarTransformer,
                                                              javaTreeTypeResolver: JavaTreeTypeResolver,
                                                              javaChildScopeResolver: JavaChildScopeResolver)
                                                             (implicit javaWriter: JavaWriter) extends DeprecatedRegularClassTraverser {

  import javaWriter._

  def traverse(classDef: Defn.Class, context: ClassOrTraitContext = ClassOrTraitContext()): Unit = {
    writeLine()
    val javaTreeType = javaTreeTypeResolver.resolve(JavaTreeTypeContext(classDef, classDef.mods))
    val modListTraversalResult = statModListTraverser.traverse(ModifiersContext(classDef, javaTreeType, context.javaScope))
    val modifiersRenderContext = modifiersRenderContextFactory(modListTraversalResult)
    modListRenderer.render(modifiersRenderContext)
    writeNamedType(JavaTreeTypeToKeywordMapping(javaTreeType), classDef.name.value)
    val traversedTypeParams = classDef.tparams.map(typeParamTraverser.traverse)
    typeParamListRenderer.render(traversedTypeParams)
    traverseCtorAndTemplate(classDef, javaTreeType, context)
  }

  private def traverseCtorAndTemplate(classDef: Defn.Class, javaTreeType: JavaTreeType, context: ClassOrTraitContext): Unit = {
    val explicitMemberDecls = classDef.ctor.paramss.flatten.map(paramToDeclVarTransformer.transform)
    // TODO if the ctor. params have 'ValParam' or 'VarParam' modifiers, need to generate accessors/mutators for them as well
    val enrichedStats = explicitMemberDecls ++ classDef.templ.stats
    val enrichedTemplate = classDef.templ.copy(stats = enrichedStats)
    val javaChildScope = javaChildScopeResolver.resolve(JavaChildScopeContext(classDef, javaTreeType))
    val templateContext = TemplateContext(
      javaScope = javaChildScope,
      maybeClassName = Some(classDef.name),
      maybePrimaryCtor = Some(classDef.ctor),
      permittedSubTypeNames = context.permittedSubTypeNames
    )
    templateTraverser.traverse(template = enrichedTemplate, context = templateContext)
  }
}
