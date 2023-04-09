package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.TermSelectContext
import io.github.effiban.scala2java.core.matchers.TermSelectContextMatcher.eqTermSelectContext
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Term, Type}

class ApplyTypeTraverserImplTest extends UnitTestSuite {

  private val classOfTraverser = mock[ClassOfTraverser]
  private val termSelectTraverser = mock[TermSelectTraverser]
  private val typeListTraverser = mock[TypeListTraverser]
  private val defaultTermTraverser = mock[DefaultTermTraverser]
  private val termApplyTraverser = mock[TermApplyTraverser]

  private val applyTypeTraverser = new ApplyTypeTraverserImpl(
    classOfTraverser,
    termSelectTraverser,
    typeListTraverser,
    defaultTermTraverser
  )


  test("traverse() when function is 'classOf' should call the dedicated traverser") {
    val typeName = Type.Name("T")
    val termApplyType = Term.ApplyType(fun = Term.Name("classOf"), targs = List(typeName))

    applyTypeTraverser.traverse(termApplyType)

    verify(classOfTraverser).traverse(eqTreeList(List(typeName)))
  }

  test("traverse() when function is a 'Select', should traverse properly") {
    val fun = Term.Select(Term.Name("myObj"), Term.Name("myFunc1"))
    val typeArgs = List(Type.Name("T1"), Type.Name("T2"))

    doWrite("myObj<T1, T2>.myFunc")
      .when(termSelectTraverser).traverse(eqTree(fun), eqTermSelectContext(TermSelectContext(typeArgs)))
    applyTypeTraverser.traverse(Term.ApplyType(fun = fun, targs = typeArgs))

    outputWriter.toString shouldBe "myObj<T1, T2>.myFunc"

    verifyNoMoreInteractions(classOfTraverser, defaultTermTraverser, termApplyTraverser)
  }

  test("traverse() when function is a 'Term.Name', should prefix with a commented 'this'") {
    val fun = Term.Name("myFunc1")
    val typeArgs = List(Type.Name("T1"), Type.Name("T2"))

    doWrite("myFunc").when(defaultTermTraverser).traverse(eqTree(fun))
    doWrite("<T1, T2>").when(typeListTraverser).traverse(eqTreeList(typeArgs))

    applyTypeTraverser.traverse(Term.ApplyType(fun = fun, targs = typeArgs))

    outputWriter.toString shouldBe "/* this? */.<T1, T2>myFunc"

    verifyNoMoreInteractions(classOfTraverser, termSelectTraverser, termApplyTraverser)
  }
}
