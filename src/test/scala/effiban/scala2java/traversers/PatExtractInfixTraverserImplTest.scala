package effiban.scala2java.traversers

import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import effiban.scala2java.testsuites.UnitTestSuite

import scala.meta.{Pat, Term}

class PatExtractInfixTraverserImplTest extends UnitTestSuite {

  private val patExtractTraverser = mock[PatExtractTraverser]

  private val patExtractInfixTraverser = new PatExtractInfixTraverserImpl(patExtractTraverser)

  test("traverse") {
    val caseClassCreator = Term.Name("MyClass")
    val lhs = Pat.Var(Term.Name("x"))
    val rhs = List(Pat.Var(Term.Name("y")), Pat.Var(Term.Name("z")))

    val patExtractInfix = Pat.ExtractInfix(lhs = lhs, op = caseClassCreator, rhs = rhs)

    val expectedPatExtract = Pat.Extract(fun = caseClassCreator, args = lhs :: rhs)

    doWrite("/* MyClass(x, y, z) */").when(patExtractTraverser).traverse(eqTree(expectedPatExtract))

    patExtractInfixTraverser.traverse(patExtractInfix)

    outputWriter.toString shouldBe "/* MyClass(x, y, z) */"
  }

}
