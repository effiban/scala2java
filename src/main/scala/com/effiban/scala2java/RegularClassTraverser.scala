package com.effiban.scala2java

import com.effiban.scala2java.TraversalContext.javaOwnerContext
import com.effiban.scala2java.transformers.ParamToDeclValTransformer

import scala.meta.Defn

trait RegularClassTraverser extends ScalaTreeTraverser[Defn.Class]

private[scala2java] class RegularClassTraverserImpl(annotListTraverser: => AnnotListTraverser,
                                                    typeParamListTraverser: => TypeParamListTraverser,
                                                    termParamListTraverser: => TermParamListTraverser,
                                                    templateTraverser: => TemplateTraverser,
                                                    paramToDeclValTransformer: ParamToDeclValTransformer,
                                                    javaModifiersResolver: JavaModifiersResolver)
                                                   (implicit javaEmitter: JavaEmitter) extends RegularClassTraverser {

  import javaEmitter._

  def traverse(classDef: Defn.Class): Unit = {
    emitLine()
    annotListTraverser.traverseMods(classDef.mods)
    emitTypeDeclaration(modifiers = javaModifiersResolver.resolveForClass(classDef.mods),
      typeKeyword = "class",
      name = classDef.name.value)
    typeParamListTraverser.traverse(classDef.tparams)
    val outerJavaOwnerContext = javaOwnerContext
    javaOwnerContext = Class
    val explicitMemberDecls = classDef.ctor.paramss.flatten.map(ParamToDeclValTransformer.transform)
    val enrichedStats = explicitMemberDecls ++ classDef.templ.stats
    val enrichedTemplate = classDef.templ.copy(stats = enrichedStats)
    templateTraverser.traverse(template = enrichedTemplate,
      maybeClassInfo = Some(ClassInfo(className = classDef.name, maybeExplicitPrimaryCtor = Some(classDef.ctor))))
    javaOwnerContext = outerJavaOwnerContext
  }
}

object RegularClassTraverser extends RegularClassTraverserImpl(
  AnnotListTraverser,
  TypeParamListTraverser,
  TermParamListTraverser,
  TemplateTraverser,
  ParamToDeclValTransformer,
  JavaModifiersResolver
)
