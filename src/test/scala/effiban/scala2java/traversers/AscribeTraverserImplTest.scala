package effiban.scala2java.traversers

import effiban.scala2java.contexts.TermContext
import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import effiban.scala2java.testsuites.UnitTestSuite
import org.mockito.ArgumentMatchers

import scala.meta.{Lit, Term, Type}

class AscribeTraverserImplTest extends UnitTestSuite {
  private val typeTraverser = mock[TypeTraverser]
  private val termTraverser = mock[TermTraverser]

  private val ascribeTraverser = new AscribeTraverserImpl(typeTraverser, termTraverser)

  test("traverse") {
    val expr = Lit.Int(22)
    val typeName = Type.Name("MyType")

    doWrite("MyType").when(typeTraverser).traverse(eqTree(typeName))
    doWrite("22").when(termTraverser).traverse(eqTree(expr), ArgumentMatchers.eq(TermContext()))

    ascribeTraverser.traverse(Term.Ascribe(expr = expr, tpe = typeName))

    outputWriter.toString shouldBe "(MyType)22"
  }
}
