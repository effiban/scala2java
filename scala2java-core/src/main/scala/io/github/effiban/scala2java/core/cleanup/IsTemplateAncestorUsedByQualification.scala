package io.github.effiban.scala2java.core.cleanup

import io.github.effiban.scala2java.core.extractors.TreeNameExtractor

import scala.meta.Term.Super
import scala.meta.{Template, Type}

object IsTemplateAncestorUsedByQualification extends IsTemplateAncestorUsed {

  def apply(template: Template, ancestorType: Type.Ref): Boolean = {
    val typeName = TreeNameExtractor.extract(ancestorType)
    template.collect { case aSuper: Super => aSuper}
      .exists(aSuper => aSuper.superp match {
        case aName if aName.value == typeName.value => true
        case _ => false
      })
  }
}
