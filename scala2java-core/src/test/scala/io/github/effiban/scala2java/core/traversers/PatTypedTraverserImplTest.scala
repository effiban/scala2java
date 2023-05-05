package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.TypeRenderer
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Pat, Term, XtensionQuasiquoteType}

class PatTypedTraverserImplTest extends UnitTestSuite {

  private val typeTraverser = mock[TypeTraverser]
  private val typeRenderer = mock[TypeRenderer]
  private val patTraverser = mock[PatTraverser]

  val patTypedTraverser = new PatTypedTraverserImpl(
    typeTraverser,
    typeRenderer,
    patTraverser
  )

  test("traverse()") {
    val lhs = Pat.Var(Term.Name("x"))
    val rhs = t"MyType"
    val traversedRhs = t"MyTraversedType"

    doReturn(traversedRhs).when(typeTraverser).traverse(eqTree(rhs))
    doWrite("MyTraversedType").when(typeRenderer).render(eqTree(traversedRhs))
    doWrite("x").when(patTraverser).traverse(eqTree(lhs))

    patTypedTraverser.traverse(Pat.Typed(lhs = lhs, rhs = rhs))

    outputWriter.toString shouldBe "MyTraversedType x"
  }
}
