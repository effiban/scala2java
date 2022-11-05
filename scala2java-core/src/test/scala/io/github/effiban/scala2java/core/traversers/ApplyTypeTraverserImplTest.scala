package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.TermSelectContext
import io.github.effiban.scala2java.core.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.core.matchers.TermSelectContextMatcher.eqTermSelectContext
import io.github.effiban.scala2java.core.matchers.TreeMatcher.eqTree
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.{Term, Type}

class ApplyTypeTraverserImplTest extends UnitTestSuite {

  private val typeTraverser = mock[TypeTraverser]
  private val termSelectTraverser = mock[TermSelectTraverser]
  private val typeListTraverser = mock[TypeListTraverser]
  private val termTraverser = mock[TermTraverser]

  private val applyTypeTraverser = new ApplyTypeTraverserImpl(
    typeTraverser,
    termSelectTraverser,
    typeListTraverser,
    termTraverser)

  test("traverse() when function is 'classOf' should convert to the Java equivalent") {
    val typeName = Type.Name("T")
    doWrite("T").when(typeTraverser).traverse(eqTree(typeName))

    applyTypeTraverser.traverse(Term.ApplyType(fun = Term.Name("classOf"), targs = List(typeName)))

    outputWriter.toString shouldBe "T.class"

    verifyNoMoreInteractions(termSelectTraverser, termTraverser)
  }

  test("traverse() when function is a Select") {
    val fun = Term.Select(Term.Name("myObj"), Term.Name("myFunc"))
    val typeArgs = List(Type.Name("T1"), Type.Name("T2"))

    doWrite("myObj<T1, T2>.myFunc")
      .when(termSelectTraverser).traverse(eqTree(fun), eqTermSelectContext(TermSelectContext(typeArgs)))

    applyTypeTraverser.traverse(Term.ApplyType(fun = fun, targs = typeArgs))

    outputWriter.toString shouldBe "myObj<T1, T2>.myFunc"

    verifyNoMoreInteractions(typeTraverser, termTraverser)
  }

  test("traverse() when function is a Term.Name") {
    val fun = Term.Name("myFunc")
    val typeArgs = List(Type.Name("T1"), Type.Name("T2"))

    doWrite("myFunc").when(termTraverser).traverse(eqTree(fun))
    doWrite("<T1, T2>").when(typeListTraverser).traverse(eqTreeList(typeArgs))

    applyTypeTraverser.traverse(Term.ApplyType(fun = fun, targs = typeArgs))

    outputWriter.toString shouldBe "/* this? */.<T1, T2>myFunc"

    verifyNoMoreInteractions(typeTraverser, termSelectTraverser)
  }
}
