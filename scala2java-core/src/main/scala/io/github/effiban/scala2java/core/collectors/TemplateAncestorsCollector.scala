package io.github.effiban.scala2java.core.collectors

import io.github.effiban.scala2java.core.extractors.TypeRefExtractor
import io.github.effiban.scala2java.core.reflection.ScalaReflectionLookup

import scala.collection.immutable.ListMap
import scala.meta.{Template, Type}

trait TemplateAncestorsCollector {
  def collect(template: Template): List[Type.Ref]

  def collectToMap(template: Template): Map[Type.Ref, List[Type.Ref]]
}

private[collectors] class TemplateAncestorsCollectorImpl(scalaReflectionLookup: ScalaReflectionLookup) extends TemplateAncestorsCollector {

  override def collect(template: Template): List[Type.Ref] = collectToMap(template).values.flatten.toList

  override def collectToMap(template: Template): Map[Type.Ref, List[Type.Ref]] = {
    ListMap.from(
      (template.inits.map(_.tpe) ++ template.self.decltpe)
        .flatMap(TypeRefExtractor.extract)
        .map(tpe => (tpe, scalaReflectionLookup.findSelfAndBaseClassesOf(tpe)))
    )
  }
}

object TemplateAncestorsCollector extends TemplateAncestorsCollectorImpl(ScalaReflectionLookup)
