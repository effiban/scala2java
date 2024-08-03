package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.{Defn, Tree, XtensionQuasiquoteTemplate, XtensionQuasiquoteTerm}

class EnclosingTemplatesInferrerImplTest extends UnitTestSuite {

  private val innermostEnclosingTemplateInferrer = mock[InnermostEnclosingTemplateInferrer]

  private val enclosingTemplatesInferrer = new EnclosingTemplatesInferrerImpl(innermostEnclosingTemplateInferrer)

  test("infer when has no enclosing templates should return empty") {
    val term = q"x"

    when(innermostEnclosingTemplateInferrer.infer(eqTree(term), eqTo(None))).thenReturn(None)

    enclosingTemplatesInferrer.infer(term) shouldBe Nil
  }

  test("infer when has one enclosing template should return it") {
    val term = q"x"
    val template = template"A"

    doAnswer((tree: Tree, _: Option[String]) => tree match {
      case q"x" => Some(template)
      case _ => None
    }).when(innermostEnclosingTemplateInferrer).infer(any[Tree], eqTo(None))

    enclosingTemplatesInferrer.infer(term).structure shouldBe List(template).structure
  }

  test("infer when has two (nested) enclosing templates should return them") {
    val term = q"x"
    val innerTemplate = template"Inner"
    val outerTemplate = template"Outer"

    doAnswer((tree: Tree, _: Option[String]) => tree match {
      case q"x" => Some(innerTemplate)
      case templ  if templ.structure == innerTemplate.structure => Some(outerTemplate)
      case _ => None
    }).when(innermostEnclosingTemplateInferrer).infer(any[Tree], eqTo(None))

    enclosingTemplatesInferrer.infer(term).structure shouldBe List(innerTemplate, outerTemplate).structure
  }
}
