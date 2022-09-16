package effiban.scala2java.traversers

import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import effiban.scala2java.testsuites.UnitTestSuite

import scala.meta.{Lit, Term}

class AssignTraverserImplTest extends UnitTestSuite {

  private val termTraverser = mock[TermTraverser]
  private val rhsTermTraverser = mock[RhsTermTraverser]

  private val assignTraverser = new AssignTraverserImpl(termTraverser, rhsTermTraverser)

  test("traverse") {
    val lhs = Term.Name("myVal")
    val rhs = Lit.Int(3)

    doWrite("myVal").when(termTraverser).traverse(eqTree(lhs))
    doWrite("3").when(rhsTermTraverser).traverse(eqTree(rhs))

    assignTraverser.traverse(Term.Assign(lhs = lhs, rhs = rhs))

    outputWriter.toString shouldBe "myVal = 3"
  }
}
