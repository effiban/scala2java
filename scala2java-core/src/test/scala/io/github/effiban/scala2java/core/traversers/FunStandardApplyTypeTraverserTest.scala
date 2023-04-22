package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.TermSelectContext
import io.github.effiban.scala2java.core.matchers.TermSelectContextMatcher.eqTermSelectContext
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Term, Type}

class FunStandardApplyTypeTraverserTest extends UnitTestSuite {

  private val funTermSelectTraverser = mock[FunTermSelectTraverser]
  private val typeListTraverser = mock[TypeListTraverser]
  private val unqualifiedTermTraverser = mock[TermTraverser]
  private val termApplyTraverser = mock[TermApplyTraverser]

  private val defaultStandardApplyTypeTraverser = new FunStandardApplyTypeTraverser(
    funTermSelectTraverser,
    typeListTraverser,
    unqualifiedTermTraverser
  )

  test("traverse() when function is a 'Select', should traverse properly") {
    val fun = Term.Select(Term.Name("myObj"), Term.Name("myFunc1"))
    val typeArgs = List(Type.Name("T1"), Type.Name("T2"))

    doWrite("myObj<T1, T2>.myFunc")
      .when(funTermSelectTraverser).traverse(eqTree(fun), eqTermSelectContext(TermSelectContext(typeArgs)))
    defaultStandardApplyTypeTraverser.traverse(Term.ApplyType(fun = fun, targs = typeArgs))

    outputWriter.toString shouldBe "myObj<T1, T2>.myFunc"

    verifyNoMoreInteractions(unqualifiedTermTraverser, termApplyTraverser)
  }

  test("traverse() when function is a 'Term.Name', should prefix with a commented 'this'") {
    val fun = Term.Name("myFunc1")
    val typeArgs = List(Type.Name("T1"), Type.Name("T2"))

    doWrite("myFunc").when(unqualifiedTermTraverser).traverse(eqTree(fun))
    doWrite("<T1, T2>").when(typeListTraverser).traverse(eqTreeList(typeArgs))

    defaultStandardApplyTypeTraverser.traverse(Term.ApplyType(fun = fun, targs = typeArgs))

    outputWriter.toString shouldBe "/* this? */.<T1, T2>myFunc"

    verifyNoMoreInteractions(funTermSelectTraverser, termApplyTraverser)
  }
}
