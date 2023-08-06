package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts._
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.spi.entities.JavaScope.JavaScope

import scala.meta.Defn

trait CaseClassTraverser {
  def traverse(classDef: Defn.Class, context: ClassOrTraitContext = ClassOrTraitContext()): Defn.Class
}

private[traversers] class CaseClassTraverserImpl(statModListTraverser: => StatModListTraverser,
                                                 typeParamTraverser: => TypeParamTraverser,
                                                 termParamTraverser: => TermParamTraverser,
                                                 templateTraverser: => TemplateTraverser) extends CaseClassTraverser {

  override def traverse(classDef: Defn.Class, context: ClassOrTraitContext = ClassOrTraitContext()): Defn.Class = {
    val traversedMods = statModListTraverser.traverse(classDef.mods)
    val traversedTypeParams = classDef.tparams.map(typeParamTraverser.traverse)
    val traversedCtorParamss = classDef.ctor.paramss.map(_.map(
      param => termParamTraverser.traverse(param, StatContext(JavaScope.Class)))
    )
    val templateTraversalResult = traverseTemplate(classDef, JavaScope.Class)

    Defn.Class(
      mods = traversedMods,
      name = classDef.name,
      tparams = traversedTypeParams,
      ctor = classDef.ctor.copy(paramss = traversedCtorParamss),
      templ = templateTraversalResult.template
    )
  }

  private def traverseTemplate(classDef: Defn.Class, javaChildScope: JavaScope) = {
    // Even though the Java type is a Record, the constructor must still be explicitly declared if it has modifiers (annotations, visibility, etc.)
    // TODO - an explicit ctor. and member will be needed also in the rare case that a param has a 'var' modifier
    val maybePrimaryCtor = if (classDef.ctor.mods.nonEmpty) Some(classDef.ctor) else None
    val templateContext = TemplateContext(
      javaScope = javaChildScope,
      maybeClassName = Some(classDef.name),
      maybePrimaryCtor = maybePrimaryCtor
    )
    templateTraverser.traverse(template = classDef.templ, context = templateContext)
  }
}
