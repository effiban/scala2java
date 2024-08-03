package io.github.effiban.scala2java.core.collectors

import io.github.effiban.scala2java.core.reflection.ScalaReflectionUtils.{asScalaMetaTypeRef, selfAndBaseClassesOf, classSymbolOf}

import scala.meta.{Template, Type}

trait TemplateAncestorsCollector {
  def collect(template: Template): List[Type.Ref]
}

object TemplateAncestorsCollector extends TemplateAncestorsCollector {

  override def collect(template: Template): List[Type.Ref] = {
    (template.inits.map(_.tpe) ++ template.self.decltpe)
      .flatMap(classSymbolOf)
      .flatMap(selfAndBaseClassesOf)
      .flatMap(asScalaMetaTypeRef)
      .distinctBy(_.structure)
  }
}
