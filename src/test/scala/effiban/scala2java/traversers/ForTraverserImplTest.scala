package effiban.scala2java.traversers

import effiban.scala2java.matchers.TreeListMatcher.eqTreeList
import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.testsuites.UnitTestSuite

import scala.meta.Enumerator.Generator
import scala.meta.Term.For
import scala.meta.{Pat, Term}

class ForTraverserImplTest extends UnitTestSuite {

  private val termTraverser = mock[TermTraverser]

  private val forTraverser = spy(new ForTraverserImpl(termTraverser))


  test("traverse") {
    val enumerators = List(
      Generator(pat = Pat.Var(Term.Name("x")), rhs = Term.Name("xs")),
      Generator(pat = Pat.Var(Term.Name("y")), rhs = Term.Name("ys"))
    )

    val body = Term.Name("result")

    val `for` = For(enums = enumerators, body = body)

    forTraverser.traverse(`for`)

    verify(forTraverser).traverse(
      enumerators = eqTreeList(enumerators),
      body = eqTree(body))
  }
}
