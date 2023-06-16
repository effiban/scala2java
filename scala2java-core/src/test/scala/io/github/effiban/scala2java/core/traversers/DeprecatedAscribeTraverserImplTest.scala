package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.TypeRenderer
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Lit, Term, XtensionQuasiquoteType}

class DeprecatedAscribeTraverserImplTest extends UnitTestSuite {
  private val typeTraverser = mock[TypeTraverser]
  private val typeRenderer = mock[TypeRenderer]
  private val expressionTermTraverser = mock[DeprecatedExpressionTermTraverser]

  private val ascribeTraverser = new DeprecatedAscribeTraverserImpl(
    typeTraverser,
    typeRenderer,
    expressionTermTraverser
  )

  test("traverse") {
    val expr = Lit.Int(22)
    val typeName = t"MyType"
    val traversedTypeName = t"MyTraversedType"

    doReturn(traversedTypeName).when(typeTraverser).traverse(eqTree(typeName))
    doWrite("MyTraversedType").when(typeRenderer).render(eqTree(traversedTypeName))
    doWrite("22").when(expressionTermTraverser).traverse(eqTree(expr))

    ascribeTraverser.traverse(Term.Ascribe(expr = expr, tpe = typeName))

    outputWriter.toString shouldBe "(MyTraversedType)22"
  }
}
