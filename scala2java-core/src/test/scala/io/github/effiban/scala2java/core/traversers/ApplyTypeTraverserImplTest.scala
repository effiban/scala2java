package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.TermSelectContext
import io.github.effiban.scala2java.core.matchers.TermSelectContextMatcher.eqTermSelectContext
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Term, Type}

class ApplyTypeTraverserImplTest extends UnitTestSuite {

  private val typeTraverser = mock[TypeTraverser]
  private val termSelectTraverser = mock[TermSelectTraverser]
  private val typeListTraverser = mock[TypeListTraverser]
  private val defaultTermTraverser = mock[DefaultTermTraverser]
  private val termApplyTraverser = mock[TermApplyTraverser]

  private val applyTypeTraverser = new ApplyTypeTraverserImpl(
    typeTraverser,
    termSelectTraverser,
    typeListTraverser,
    defaultTermTraverser
  )


  test("traverse() when function is 'classOf' should convert to the Java equivalent") {
    val typeName = Type.Name("T")
    val termApplyType = Term.ApplyType(fun = Term.Name("classOf"), targs = List(typeName))

    doWrite("T").when(typeTraverser).traverse(eqTree(typeName))

    applyTypeTraverser.traverse(termApplyType)

    outputWriter.toString shouldBe "T.class"

    verifyNoMoreInteractions(termSelectTraverser, defaultTermTraverser, termApplyTraverser)
  }

  test("traverse() when function is a 'Select', should traverse properly") {
    val fun = Term.Select(Term.Name("myObj"), Term.Name("myFunc1"))
    val typeArgs = List(Type.Name("T1"), Type.Name("T2"))
    val termApplyType = Term.ApplyType(fun = fun, targs = typeArgs)

    doWrite("myObj<T1, T2>.myFunc")
      .when(termSelectTraverser).traverse(eqTree(fun), eqTermSelectContext(TermSelectContext(typeArgs)))
    applyTypeTraverser.traverse(Term.ApplyType(fun = fun, targs = typeArgs))

    outputWriter.toString shouldBe "myObj<T1, T2>.myFunc"

    verifyNoMoreInteractions(typeTraverser, defaultTermTraverser, termApplyTraverser)
  }

  test("traverse() when when function is a 'Term.Name', should prefix with a commented 'this'") {
    val fun = Term.Name("myFunc1")
    val typeArgs = List(Type.Name("T1"), Type.Name("T2"))
    val termApplyType = Term.ApplyType(fun = fun, targs = typeArgs)

    doWrite("myFunc").when(defaultTermTraverser).traverse(eqTree(fun))
    doWrite("<T1, T2>").when(typeListTraverser).traverse(eqTreeList(typeArgs))

    applyTypeTraverser.traverse(Term.ApplyType(fun = fun, targs = typeArgs))

    outputWriter.toString shouldBe "/* this? */.<T1, T2>myFunc"

    verifyNoMoreInteractions(typeTraverser, termSelectTraverser, termApplyTraverser)
  }
}
