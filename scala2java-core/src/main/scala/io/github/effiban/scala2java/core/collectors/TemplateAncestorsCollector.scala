package io.github.effiban.scala2java.core.collectors

import io.github.effiban.scala2java.core.reflection.ScalaReflectionUtils.{asScalaMetaTypeRef, selfAndBaseClassesOf, classSymbolOf}

import scala.collection.immutable.ListMap
import scala.meta.{Template, Type}

trait TemplateAncestorsCollector {
  def collect(template: Template): List[Type.Ref]

  def collectToMap(template: Template): Map[Type.Ref, List[Type.Ref]]
}

object TemplateAncestorsCollector extends TemplateAncestorsCollector {

  override def collect(template: Template): List[Type.Ref] = collectToMap(template).values.flatten.toList

  override def collectToMap(template: Template): ListMap[Type.Ref, List[Type.Ref]] = {
    ListMap.from(
      (template.inits.map(_.tpe) ++ template.self.decltpe)
        .flatMap(classSymbolOf)
        .map(classSymbol => (classSymbol, asScalaMetaTypeRef(classSymbol)))
        .collect { case (clsSym, Some(clsType)) => (clsSym, clsType) }
        .map { case (clsSym, clsType) => (clsType, selfAndBaseClassesOf(clsSym).flatMap(asScalaMetaTypeRef)) }
        .map { case (clsType, baseClsTypes) => (clsType, baseClsTypes.distinctBy(_.structure)) }
    )
  }
}
