package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.TermSelectContext
import io.github.effiban.scala2java.core.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.core.matchers.TermSelectContextMatcher.eqTermSelectContext
import io.github.effiban.scala2java.core.matchers.TreeMatcher.eqTree
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.transformers.TermApplyTypeToTermApplyTransformer

import scala.meta.{Term, Type}

class ApplyTypeTraverserImplTest extends UnitTestSuite {

  private val typeTraverser = mock[TypeTraverser]
  private val termSelectTraverser = mock[TermSelectTraverser]
  private val typeListTraverser = mock[TypeListTraverser]
  private val termTraverser = mock[TermTraverser]
  private val termApplyTraverser = mock[TermApplyTraverser]
  private val termApplyTypeToTermApplyTransformer = mock[TermApplyTypeToTermApplyTransformer]

  private val applyTypeTraverser = new ApplyTypeTraverserImpl(
    typeTraverser,
    termSelectTraverser,
    typeListTraverser,
    termTraverser,
    termApplyTraverser,
    termApplyTypeToTermApplyTransformer)


  test("traverse() when not transformed and function is 'classOf' should convert to the Java equivalent") {
    val typeName = Type.Name("T")
    val termApplyType = Term.ApplyType(fun = Term.Name("classOf"), targs = List(typeName))

    when(termApplyTypeToTermApplyTransformer.transform(termApplyType)).thenReturn(None)
    doWrite("T").when(typeTraverser).traverse(eqTree(typeName))

    applyTypeTraverser.traverse(termApplyType)

    outputWriter.toString shouldBe "T.class"

    verifyNoMoreInteractions(termSelectTraverser, termTraverser, termApplyTraverser)
  }

  test("traverse() when not transformed and function is a 'Select', should traverse properly") {
    val fun = Term.Select(Term.Name("myObj"), Term.Name("myFunc1"))
    val typeArgs = List(Type.Name("T1"), Type.Name("T2"))
    val termApplyType = Term.ApplyType(fun = fun, targs = typeArgs)

    when(termApplyTypeToTermApplyTransformer.transform(eqTree(termApplyType))).thenReturn(None)
    doWrite("myObj<T1, T2>.myFunc")
      .when(termSelectTraverser).traverse(eqTree(fun), eqTermSelectContext(TermSelectContext(typeArgs)))
    applyTypeTraverser.traverse(Term.ApplyType(fun = fun, targs = typeArgs))

    outputWriter.toString shouldBe "myObj<T1, T2>.myFunc"

    verifyNoMoreInteractions(typeTraverser, termTraverser, termApplyTraverser)
  }

  test("traverse() when when not transformed and function is a 'Term.Name', should prefix with a commented 'this'") {
    val fun = Term.Name("myFunc1")
    val typeArgs = List(Type.Name("T1"), Type.Name("T2"))
    val termApplyType = Term.ApplyType(fun = fun, targs = typeArgs)

    when(termApplyTypeToTermApplyTransformer.transform(eqTree(termApplyType))).thenReturn(None)
    doWrite("myFunc").when(termTraverser).traverse(eqTree(fun))
    doWrite("<T1, T2>").when(typeListTraverser).traverse(eqTreeList(typeArgs))

    applyTypeTraverser.traverse(Term.ApplyType(fun = fun, targs = typeArgs))

    outputWriter.toString shouldBe "/* this? */.<T1, T2>myFunc"

    verifyNoMoreInteractions(typeTraverser, termSelectTraverser, termApplyTraverser)
  }

  test("traverse() when transformed should call the 'TermApplyTraverser'") {
    val fun = Term.Name("myFunc")
    val typeArgs = List(Type.Name("T"))
    val termApplyType = Term.ApplyType(fun = fun, targs = typeArgs)
    val termApply = Term.Apply(fun, List(Term.ApplyType(Term.Name("classOf"), typeArgs)))

    when(termApplyTypeToTermApplyTransformer.transform(eqTree(termApplyType))).thenReturn(Some(termApply))
    doWrite("myFunc(classOf[T])")
      .when(termApplyTraverser).traverse(eqTree(termApply))

    applyTypeTraverser.traverse(Term.ApplyType(fun = fun, targs = typeArgs))

    outputWriter.toString shouldBe "myFunc(classOf[T])"

    verifyNoMoreInteractions(typeTraverser, termTraverser, termSelectTraverser)
  }
}
