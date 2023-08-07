package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.TemplateContext
import io.github.effiban.scala2java.core.matchers.TemplateContextMatcher.eqTemplateContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{XtensionQuasiquoteTemplate, XtensionQuasiquoteTerm}

class NewAnonymousTraverserImplTest extends UnitTestSuite {

  private val templateTraverser = mock[TemplateTraverser]

  private val newAnonymousTraverser = new NewAnonymousTraverserImpl(templateTraverser)

  test("traverse()") {
    val template = template"MyTrait { override def foo(x: Int) = x + 1 }"
    val expectedTraversedTemplate = template"MyTraversedTrait { override def traversedFoo(x: Int) = x + 1 }"

    val newAnonymous = q"new MyTrait { override def foo(x: Int) = x + 1 }"
    val traversedNewAnonymous = q"new MyTraversedTrait { override def traversedFoo(x: Int) = x + 1 }"

    doReturn(expectedTraversedTemplate)
      .when(templateTraverser).traverse(eqTree(template), eqTemplateContext(TemplateContext(JavaScope.Class)))

    newAnonymousTraverser.traverse(newAnonymous).structure shouldBe traversedNewAnonymous.structure
  }
}
