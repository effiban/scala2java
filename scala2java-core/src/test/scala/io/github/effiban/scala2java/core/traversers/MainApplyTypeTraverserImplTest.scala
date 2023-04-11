package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TermNames.ScalaClassOf
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Term, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class MainApplyTypeTraverserImplTest extends UnitTestSuite {

  private val classOfTraverser = mock[ClassOfTraverser]
  private val standardApplyTypeTraverser = mock[StandardApplyTypeTraverser]

  private val mainApplyTypeTraverser = new MainApplyTypeTraverserImpl(
    classOfTraverser,
    standardApplyTypeTraverser
  )


  test("traverse() when function is 'classOf' should call the dedicated traverser") {
    val typeName = t"T"
    val termApplyType = Term.ApplyType(fun = ScalaClassOf, targs = List(typeName))

    mainApplyTypeTraverser.traverse(termApplyType)

    verify(classOfTraverser).traverse(eqTreeList(List(typeName)))
  }

  test("traverse() when function is 'foo', should call the standard traverser") {
    val termApplyType = q"foo[T1, T2]"

    mainApplyTypeTraverser.traverse(termApplyType)

    verify(standardApplyTypeTraverser).traverse(eqTree(termApplyType))
  }
}
