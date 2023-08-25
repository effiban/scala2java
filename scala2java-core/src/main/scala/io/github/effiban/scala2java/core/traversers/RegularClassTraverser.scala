package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts._
import io.github.effiban.scala2java.core.entities.JavaTreeType.JavaTreeType
import io.github.effiban.scala2java.core.resolvers.{JavaChildScopeResolver, JavaTreeTypeResolver}
import io.github.effiban.scala2java.core.transformers.ParamToDeclVarTransformer

import scala.meta.{Ctor, Defn}

trait RegularClassTraverser {
  def traverse(classDef: Defn.Class, context: ClassOrTraitContext = ClassOrTraitContext()): Defn.Class
}

private[traversers] class RegularClassTraverserImpl(statModListTraverser: => StatModListTraverser,
                                                    typeParamTraverser: => TypeParamTraverser,
                                                    templateTraverser: => TemplateTraverser,
                                                    paramToDeclVarTransformer: ParamToDeclVarTransformer,
                                                    javaTreeTypeResolver: JavaTreeTypeResolver,
                                                    javaChildScopeResolver: JavaChildScopeResolver) extends RegularClassTraverser {

  def traverse(classDef: Defn.Class, context: ClassOrTraitContext = ClassOrTraitContext()): Defn.Class = {
    val javaTreeType = javaTreeTypeResolver.resolve(JavaTreeTypeContext(classDef, classDef.mods))
    val traversedMods = statModListTraverser.traverse(classDef.mods)
    val traversedTypeParams = classDef.tparams.map(typeParamTraverser.traverse)
    val traversedTemplate = traverseCtorAndTemplate(classDef, javaTreeType)

    Defn.Class(
      mods = traversedMods,
      name = classDef.name,
      tparams = traversedTypeParams,
      ctor = clearCtorArgs(classDef.ctor),
      templ = traversedTemplate
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

  private def clearCtorArgs(ctorPrimary: Ctor.Primary) = {
    // Clearing the arguments because they have just been copied into explicit arguments and members to match the Java style,
    // and we don't want to manipulate them any further.
    ctorPrimary.copy(paramss = ctorPrimary.paramss.map(_ => Nil))
  }
}
