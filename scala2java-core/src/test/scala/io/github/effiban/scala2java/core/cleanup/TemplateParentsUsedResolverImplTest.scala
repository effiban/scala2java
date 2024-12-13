package io.github.effiban.scala2java.core.cleanup

import io.github.effiban.scala2java.core.collectors.TemplateAncestorsCollector
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.{Template, Type, XtensionQuasiquoteTemplate, XtensionQuasiquoteType}

class TemplateParentsUsedResolverImplTest extends UnitTestSuite {

  private val templateAncestorsCollector = mock[TemplateAncestorsCollector]
  private val isTemplateAncestorUsed = mock[IsTemplateAncestorUsed]

  private val templateParentsUsedResolver = new TemplateParentsUsedResolverImpl(templateAncestorsCollector, isTemplateAncestorUsed)

  test("resolve() when has two qualified parents and none used, should return empty") {

    val template =
      template"""
      qualA.A1 with qualB.B1
      """

    when(templateAncestorsCollector.collectToMap(template)).thenReturn(
      Map(t"qualA.A1" -> List(t"qualA.A1", t"qualA.B2"),
          t"qualB.B1" -> List(t"qualB.B1", t"qualB.B2")
      )
    )

    when(isTemplateAncestorUsed(eqTree(template), any[Type.Ref])).thenReturn(false)

    templateParentsUsedResolver.resolve(template) shouldBe Nil

  }

  test("resolve() when has two qualified parents, and some used from one parent only, should return that parent") {

    val template =
      template"""
      qualA.A1 with qualB.B1
      """

    when(templateAncestorsCollector.collectToMap(template)).thenReturn(
      Map(
        t"qualA.A1" -> List(t"qualA.A1", t"qualA.A2"),
        t"qualB.B1" -> List(t"qualB.B1", t"qualB.B2")
      ),
    )

    doAnswer((_: Template, ancestor: Type.Ref) => ancestor match {
      case t"qualA.A1" => true
      case _ => false
    }).when(isTemplateAncestorUsed)(eqTree(template), any[Type.Ref])

    templateParentsUsedResolver.resolve(template).structure shouldBe List(t"qualA.A1").structure
  }

  test("resolve() when has two qualified parents, and all used from one parent only, should return that parent") {

    val template =
      template"""
      qualA.A1 with qualB.B1
      """

    when(templateAncestorsCollector.collectToMap(template)).thenReturn(
      Map(
        t"qualA.A1" -> List(t"qualA.A1", t"qualA.A2"),
        t"qualB.B1" -> List(t"qualB.B1", t"qualB.B2")
      ),
    )

    doAnswer((_: Template, ancestor: Type.Ref) => ancestor match {
      case t"qualA.A1" | t"qualA.A2" => true
      case _ => false
    }).when(isTemplateAncestorUsed)(eqTree(template), any[Type.Ref])

    templateParentsUsedResolver.resolve(template).structure shouldBe List(t"qualA.A1").structure
  }

  test("resolve() when has two qualified parents, and some used from both parents, should return both parents") {

    val template =
      template"""
      qualA.A1 with qualB.B1
      """

    when(templateAncestorsCollector.collectToMap(template)).thenReturn(
      Map(
        t"qualA.A1" -> List(t"qualA.A1", t"qualA.A2"),
        t"qualB.B1" -> List(t"qualB.B1", t"qualB.B2")
      ),
    )

    doAnswer((_: Template, ancestor: Type.Ref) => ancestor match {
      case t"qualA.A1" | t"qualB.B2" => true
      case _ => false
    }).when(isTemplateAncestorUsed)(eqTree(template), any[Type.Ref])

    templateParentsUsedResolver.resolve(template).structure shouldBe List(t"qualA.A1", t"qualB.B1").structure
  }

  test("resolve() when has two unqualified (and usued) parents should return them") {

    val template =
      template"""
      A1 with B1
      """

    when(templateAncestorsCollector.collectToMap(template)).thenReturn(
      Map(
        t"A1" -> List(t"A1", t"A2"),
        t"B1" -> List(t"B1", t"B2")
      ),
    )

    when(isTemplateAncestorUsed(eqTree(template), any[Type.Ref])).thenReturn(false)

    templateParentsUsedResolver.resolve(template).structure shouldBe List(t"A1", t"B1").structure
  }

  test("resolve() when has two qualified and used parents, and two unqualified (and unused) parents - should return them all") {

    val template =
      template"""
      qualA.A1 with qualB.B1 with C1 with D1
      """

    when(templateAncestorsCollector.collectToMap(template)).thenReturn(
      Map(
        t"qualA.A1" -> List(t"qualA.A1", t"qualA.A2"),
        t"qualB.B1" -> List(t"qualB.B1", t"qualB.B2"),
        t"C1" -> List(t"C1", t"C2"),
        t"D1" -> List(t"D1", t"D2")
      ),
    )

    doAnswer((_: Template, ancestor: Type.Ref) => ancestor match {
      case t"qualA.A1" | t"qualB.B2" => true
      case _ => false
    }).when(isTemplateAncestorUsed)(eqTree(template), any[Type.Ref])

    templateParentsUsedResolver.resolve(template).structure shouldBe
      List(t"qualA.A1", t"qualB.B1", t"C1", t"D1").structure
  }
}
