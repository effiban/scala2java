package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.TemplateContext
import io.github.effiban.scala2java.core.matchers.TemplateContextMatcher.eqTemplateContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.traversers.results.{DefnDefTraversalResult, TemplateTraversalResult}
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{XtensionQuasiquoteInit, XtensionQuasiquoteTemplate, XtensionQuasiquoteTerm}

class NewAnonymousTraverserImplTest extends UnitTestSuite {

  private val templateTraverser = mock[TemplateTraverser]

  private val newAnonymousTraverser = new NewAnonymousTraverserImpl(templateTraverser)

  test("traverse()") {
    val defnDefTraversalResult = DefnDefTraversalResult(q"override def traversedFoo(x: Int) = x + 1")

    val template = template"MyTrait { override def foo(x: Int) = x + 1 }"
    val templateTraversalResult = TemplateTraversalResult(
      inits = List(init"MyTraversedTrait"),
      statResults = List(defnDefTraversalResult)
    )

    val newAnonymous = q"new MyTrait { override def foo(x: Int) = x + 1 }"
    val traversedNewAnonymous = q"new MyTraversedTrait { override def traversedFoo(x: Int) = x + 1 }"

    doReturn(templateTraversalResult)
      .when(templateTraverser).traverse(eqTree(template), eqTemplateContext(TemplateContext(JavaScope.Class)))

    newAnonymousTraverser.traverse(newAnonymous).structure shouldBe traversedNewAnonymous.structure
  }
}
