package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.{Lit, Term}

class AssignLHSTraverserImplTest extends UnitTestSuite {

  private val termTraverser = mock[TermTraverser]

  private val assignLHSTraverser = new AssignLHSTraverserImpl(termTraverser)

  test("traverse when LHS should be traversed normally") {
    val lhs = Term.Name("myVal")

    doWrite("myVal").when(termTraverser).traverse(eqTree(lhs))

    assignLHSTraverser.traverse(lhs)

    outputWriter.toString shouldBe "myVal = "
  }

  test("traverse when LHS should be written as a comment") {
    val lhs = Term.Name("myVal")

    assignLHSTraverser.traverse(lhs, asComment = true)

    outputWriter.toString shouldBe "/* myVal = */"
  }}
