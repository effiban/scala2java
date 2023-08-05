package io.github.effiban.scala2java.core.resolvers

import io.github.effiban.scala2java.core.classifiers.ModsClassifier
import io.github.effiban.scala2java.core.entities.SealedHierarchies

import scala.meta.{Defn, Member, Name, Stat, Template}

trait SealedHierarchiesResolver {
  def resolve(stats: List[Stat]): SealedHierarchies
}

private[resolvers] class SealedHierarchiesResolverImpl(modsClassifier: ModsClassifier) extends SealedHierarchiesResolver {

  override def resolve(stats: List[Stat]): SealedHierarchies = {
    val sealedTypes: List[Member.Type] = stats.collect {
      case defnClass: Defn.Class if modsClassifier.includeSealed(defnClass.mods) => defnClass
      case defnTrait: Defn.Trait if modsClassifier.includeSealed(defnTrait.mods) => defnTrait
    }

    val sealedNameToSubTypeNames = stats.collect(templateAndName())
      .map { case (template, name) => (sealedParentNameOf(sealedTypes, template), name) }
      .collect { case (Some(sealedName), subTypeName) => (sealedName, subTypeName) }
      .groupMap(_._1)(_._2)
    SealedHierarchies(sealedNameToSubTypeNames)
  }

  private def templateAndName(): PartialFunction[Stat, (Template, Name)] = {
      case defnClass: Defn.Class => (defnClass.templ, defnClass.name)
      case defnTrait: Defn.Trait => (defnTrait.templ, defnTrait.name)
      case defnObject: Defn.Object => (defnObject.templ, defnObject.name)
  }

  private def sealedParentNameOf(sealedTypes: List[Member.Type], template: Template) = {
    sealedTypes.find(sealedType => inheritsFrom(template, sealedType)).map(_.name)
  }

  private def inheritsFrom(template: Template, sealedType: Member.Type): Boolean =
    template.inits.exists(_.tpe.structure == sealedType.name.structure)

}

object SealedHierarchiesResolver extends SealedHierarchiesResolverImpl(ModsClassifier)