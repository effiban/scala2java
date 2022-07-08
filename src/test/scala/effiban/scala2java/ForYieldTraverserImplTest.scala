package effiban.scala2java

import effiban.scala2java.matchers.TreeListMatcher.eqTreeList
import effiban.scala2java.matchers.TreeMatcher.eqTree

import scala.meta.Enumerator.Generator
import scala.meta.Term.ForYield
import scala.meta.{Pat, Term}

class ForYieldTraverserImplTest extends UnitTestSuite {

  private val forVariantTraverser = mock[ForVariantTraverser]

  private val forYieldTraverser = new ForYieldTraverserImpl(forVariantTraverser)

  test("traverse") {
    val enumerators = List(
      Generator(pat = Pat.Var(Term.Name("x")), rhs = Term.Name("xs")),
      Generator(pat = Pat.Var(Term.Name("y")), rhs = Term.Name("ys"))
    )

    val body = Term.Name("result")

    val forYield = ForYield(
      enums = enumerators,
      body = body
    )
    forYieldTraverser.traverse(forYield)

    verify(forVariantTraverser).traverse(
      enumerators = eqTreeList(enumerators),
      body = eqTree(body),
      finalFunctionName = eqTree(Term.Name("map")))
  }
}
