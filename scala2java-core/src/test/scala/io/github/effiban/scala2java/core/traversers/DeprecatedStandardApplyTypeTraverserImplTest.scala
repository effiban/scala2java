package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.TermSelectContext
import io.github.effiban.scala2java.core.matchers.TermSelectContextMatcher.eqTermSelectContext
import io.github.effiban.scala2java.core.renderers.TypeListRenderer
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Term, Type, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class DeprecatedStandardApplyTypeTraverserImplTest extends UnitTestSuite {

  private val funTermSelectTraverser = mock[DeprecatedFunTermSelectTraverser]
  private val typeTraverser = mock[TypeTraverser]
  private val typeListRenderer = mock[TypeListRenderer]
  private val unqualifiedTermTraverser = mock[DeprecatedTermTraverser]
  private val termApplyTraverser = mock[DeprecatedTermApplyTraverser]

  private val standardApplyTypeTraverser = new DeprecatedStandardApplyTypeTraverserImpl(
    funTermSelectTraverser,
    typeTraverser,
    typeListRenderer,
    unqualifiedTermTraverser
  )

  test("traverse() when function is a 'Select', should traverse properly") {
    val fun = Term.Select(Term.Name("myObj"), Term.Name("myFunc1"))
    val typeArgs = List(Type.Name("T1"), Type.Name("T2"))

    doWrite("myObj<T1, T2>.myFunc")
      .when(funTermSelectTraverser).traverse(eqTree(fun), eqTermSelectContext(TermSelectContext(typeArgs)))
    standardApplyTypeTraverser.traverse(Term.ApplyType(fun = fun, targs = typeArgs))

    outputWriter.toString shouldBe "myObj<T1, T2>.myFunc"

    verifyNoMoreInteractions(unqualifiedTermTraverser, termApplyTraverser)
  }

  test("traverse() when function is a 'Term.Name', should prefix with a commented 'this'") {
    val fun = q"myFunc1"

    val typeArg1 = t"T1"
    val typeArg2 = t"T2"
    val typeArgs = List(typeArg1, typeArg2)

    val traversedTypeArg1 = t"U1"
    val traversedTypeArg2 = t"U2"
    val traversedTypeArgs = List(traversedTypeArg1, traversedTypeArg2)

    doWrite("myFunc").when(unqualifiedTermTraverser).traverse(eqTree(fun))
    doReturn(traversedTypeArg1).when(typeTraverser).traverse(eqTree(typeArg1))
    doReturn(traversedTypeArg2).when(typeTraverser).traverse(eqTree(typeArg2))
    doWrite("<U1, U2>").when(typeListRenderer).render(eqTreeList(traversedTypeArgs))

    standardApplyTypeTraverser.traverse(Term.ApplyType(fun = fun, targs = typeArgs))

    outputWriter.toString shouldBe "/* this? */.<U1, U2>myFunc"

    verifyNoMoreInteractions(funTermSelectTraverser, termApplyTraverser)
  }
}
