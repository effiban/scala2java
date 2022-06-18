package com.effiban.scala2java

import com.effiban.scala2java.matchers.TreeListMatcher.eqTreeList
import com.effiban.scala2java.matchers.TreeMatcher.eqTree

import scala.meta.Enumerator.Generator
import scala.meta.Term.For
import scala.meta.{Pat, Term}

class ForTraverserImplTest extends UnitTestSuite {

  private val forVariantTraverser = mock[ForVariantTraverser]

  private val forTraverser = new ForTraverserImpl(forVariantTraverser)


  test("traverse") {
    val enumerators = List(
      Generator(pat = Pat.Var(Term.Name("x")), rhs = Term.Name("xs")),
      Generator(pat = Pat.Var(Term.Name("y")), rhs = Term.Name("ys"))
    )

    val body = Term.Name("result")

    val `for` = For(enums = enumerators, body = body)

    forTraverser.traverse(`for`)

    verify(forVariantTraverser).traverse(
      enumerators = eqTreeList(enumerators),
      body = eqTree(body),
      finalFunctionName = eqTree(Term.Name("forEach")))
  }
}
