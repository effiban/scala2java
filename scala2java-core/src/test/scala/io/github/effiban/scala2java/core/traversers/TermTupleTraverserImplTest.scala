package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.Term.Tuple
import scala.meta.{Term, XtensionQuasiquoteTerm}

class TermTupleTraverserImplTest extends UnitTestSuite {

  private val expressionTermTraverser = mock[ExpressionTermTraverser]

  private val termTupleTraverser = new TermTupleTraverserImpl(expressionTermTraverser)

  test("traverse") {
    val args = List(q"1", q"2")
    val traversedArgs = List(q"11", q"22")
    val tuple = Tuple(args)
    val expectedTraversedTuple = Tuple(traversedArgs)

    doAnswer((arg: Term) => arg match {
      case q"1" => q"11"
      case q"2" => q"22"
      case other => other
    }).when(expressionTermTraverser).traverse(any[Term])

    termTupleTraverser.traverse(tuple).structure shouldBe expectedTraversedTuple.structure
  }
}
