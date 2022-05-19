package com.effiban.scala2java.stubs

import com.effiban.scala2java.{ClassInfo, JavaEmitter, TemplateTraverser}

import scala.meta.Template

class StubTemplateTraverser(implicit javaEmitter: JavaEmitter) extends TemplateTraverser {

  import javaEmitter._

  override def traverse(template: Template): Unit = {
    traverse(template, None)
  }

  override def traverse(template: Template,
                        maybeClassInfo: Option[ClassInfo] = None): Unit = {
    emitLine()
    emitComment(
      s"""STUB TEMPLATE
         |Input ClassInfo: ${maybeClassInfo.getOrElse("None")}
         |Scala Body:${templateToString(template)}""".stripMargin
    )
  }

  private def templateToString(template: Template) = {
    val templateStr = template.toString()
    if (templateStr.isBlank) " None" else s"\n$templateStr"
  }
}
