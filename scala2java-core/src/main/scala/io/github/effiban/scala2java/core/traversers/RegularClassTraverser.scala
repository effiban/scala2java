package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts._
import io.github.effiban.scala2java.core.entities.JavaTreeType.JavaTreeType
import io.github.effiban.scala2java.core.entities.JavaTreeTypeToKeywordMapping
import io.github.effiban.scala2java.core.resolvers.{JavaChildScopeResolver, JavaTreeTypeResolver}
import io.github.effiban.scala2java.core.transformers.ParamToDeclVarTransformer
import io.github.effiban.scala2java.core.traversers.results.RegularClassTraversalResult

import scala.meta.Defn

trait RegularClassTraverser {
  def traverse(classDef: Defn.Class, context: ClassOrTraitContext = ClassOrTraitContext()): RegularClassTraversalResult
}

private[traversers] class RegularClassTraverserImpl(statModListTraverser: => StatModListTraverser,
                                                    typeParamTraverser: => TypeParamTraverser,
                                                    templateTraverser: => TemplateTraverser,
                                                    paramToDeclVarTransformer: ParamToDeclVarTransformer,
                                                    javaTreeTypeResolver: JavaTreeTypeResolver,
                                                    javaChildScopeResolver: JavaChildScopeResolver) extends RegularClassTraverser {

  def traverse(classDef: Defn.Class, context: ClassOrTraitContext = ClassOrTraitContext()): RegularClassTraversalResult = {
    val javaTreeType = javaTreeTypeResolver.resolve(JavaTreeTypeContext(classDef, classDef.mods))
    val modListTraversalResult = statModListTraverser.traverse(ModifiersContext(classDef, javaTreeType, context.javaScope))
    val javaTypeKeyword = JavaTreeTypeToKeywordMapping(javaTreeType)
    val traversedTypeParams = classDef.tparams.map(typeParamTraverser.traverse)
    val templateTraversalResult = traverseCtorAndTemplate(classDef, javaTreeType)

    RegularClassTraversalResult(
      scalaMods = modListTraversalResult.scalaMods,
      javaModifiers = modListTraversalResult.javaModifiers,
      javaTypeKeyword = javaTypeKeyword,
      name = classDef.name,
      tparams = traversedTypeParams,
      ctor = classDef.ctor,
      maybeInheritanceKeyword = templateTraversalResult.maybeInheritanceKeyword,
      inits = templateTraversalResult.inits,
      self = templateTraversalResult.self,
      statResults = templateTraversalResult.statResults
    )
  }

  private def traverseCtorAndTemplate(classDef: Defn.Class, javaTreeType: JavaTreeType) = {
    val explicitMemberDecls = classDef.ctor.paramss.flatten.map(paramToDeclVarTransformer.transform)
    // TODO if the ctor. params have 'ValParam' or 'VarParam' modifiers, need to generate accessors/mutators for them as well
    val enrichedStats = explicitMemberDecls ++ classDef.templ.stats
    val enrichedTemplate = classDef.templ.copy(stats = enrichedStats)
    val javaChildScope = javaChildScopeResolver.resolve(JavaChildScopeContext(classDef, javaTreeType))
    val templateContext = TemplateContext(
      javaScope = javaChildScope,
      maybeClassName = Some(classDef.name),
      maybePrimaryCtor = Some(classDef.ctor)
    )
    templateTraverser.traverse(template = enrichedTemplate, context = templateContext)
  }
}
