package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.matchers.TermSelectContextMatcher.eqTermSelectContext
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TypeNames
import io.github.effiban.scala2java.core.typeinference.TermTypeInferrer
import io.github.effiban.scala2java.spi.contexts.TermSelectContext
import io.github.effiban.scala2java.spi.transformers.TermSelectTransformer
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Lit, Term, XtensionQuasiquoteType}

class TermSelectTraverserImplTest extends UnitTestSuite {

  private val MyInstance = Term.Name("MyObject")
  private val MyType = t"MyType"
  private val MyMethod = Term.Name("myMethod")
  private val MyMethod2 = Term.Name("myMethod2")
  private val MyJavaClass = Term.Name("MyJavaClass")
  private val MyJavaMethod = Term.Name("myJavaMethod")
  private val MyJavaMethod2 = Term.Name("myJavaMethod2")
  private val ScalaSelectWithTermName = Term.Select(qual = MyInstance, name = MyMethod)
  private val JavaSelectWithTermName = Term.Select(qual = MyJavaClass, name = MyJavaMethod)

  private val termTraverser = mock[TermTraverser]
  private val termNameTraverser = mock[TermNameTraverser]
  private val typeListTraverser = mock[TypeListTraverser]
  private val termTypeInferrer = mock[TermTypeInferrer]
  private val termSelectTransformer = mock[TermSelectTransformer]

  private val termSelectTraverser = new TermSelectTraverserImpl(
    termTraverser,
    termNameTraverser,
    typeListTraverser,
    termTypeInferrer,
    termSelectTransformer
  )

  test("traverse() when qualifier is a Term.Name, and has type args and inferred qualifier type") {
    val typeArgs = List(TypeNames.Int)
    val inputContext = TermSelectContext(appliedTypeArgs = typeArgs)
    val expectedAdjustedContext = TermSelectContext(appliedTypeArgs = typeArgs, maybeQualType = Some(MyType))

    when(termTypeInferrer.infer(eqTree(MyInstance))).thenReturn(Some(MyType))
    when(termSelectTransformer.transform(eqTree(ScalaSelectWithTermName), eqTermSelectContext(expectedAdjustedContext)))
      .thenReturn(JavaSelectWithTermName)

    doWrite("MyJavaClass").when(termTraverser).traverse(eqTree(MyJavaClass))
    doWrite("myJavaMethod").when(termNameTraverser).traverse(eqTree(MyJavaMethod))
    doWrite("<Integer>").when(typeListTraverser).traverse(eqTreeList(typeArgs))

    termSelectTraverser.traverse(ScalaSelectWithTermName, inputContext)

    outputWriter.toString shouldBe "MyJavaClass.<Integer>myJavaMethod"
  }

  test("traverse() when qualifier is a Term.Name, and has type args but no inferred qualifier type") {
    val typeArgs = List(TypeNames.Int)
    val context = TermSelectContext(appliedTypeArgs = typeArgs)

    when(termTypeInferrer.infer(eqTree(MyInstance))).thenReturn(None)
    when(termSelectTransformer.transform(eqTree(ScalaSelectWithTermName), eqTermSelectContext(context)))
      .thenReturn(JavaSelectWithTermName)

    doWrite("MyJavaClass").when(termTraverser).traverse(eqTree(MyJavaClass))
    doWrite("myJavaMethod").when(termNameTraverser).traverse(eqTree(MyJavaMethod))
    doWrite("<Integer>").when(typeListTraverser).traverse(eqTreeList(typeArgs))

    termSelectTraverser.traverse(ScalaSelectWithTermName, context)

    outputWriter.toString shouldBe "MyJavaClass.<Integer>myJavaMethod"
  }

  test("traverse() when qualifier is a Term.Name and has no type args and no inferred qualifier type") {
    when(termTypeInferrer.infer(eqTree(MyInstance))).thenReturn(None)
    when(termSelectTransformer.transform(eqTree(ScalaSelectWithTermName), eqTermSelectContext(TermSelectContext())))
      .thenReturn(JavaSelectWithTermName)

    doWrite("MyJavaClass").when(termTraverser).traverse(eqTree(MyJavaClass))
    doWrite("myJavaMethod").when(termNameTraverser).traverse(eqTree(MyJavaMethod))

    termSelectTraverser.traverse(ScalaSelectWithTermName)

    outputWriter.toString shouldBe "MyJavaClass.myJavaMethod"
  }

  test("traverse() when qualifier is a Term.Function should wrap in parentheses") {
    val termFunction = Term.Function(Nil, Lit.Int(1))
    val scalaSelect = Term.Select(termFunction, MyMethod)
    val javaSelect = Term.Select(termFunction, MyJavaMethod)

    when(termTypeInferrer.infer(eqTree(termFunction))).thenReturn(None)
    when(termSelectTransformer.transform(eqTree(scalaSelect), eqTermSelectContext(TermSelectContext()))).thenReturn(javaSelect)

    doWrite("() -> 1").when(termTraverser).traverse(eqTree(termFunction))
    doWrite("get").when(termNameTraverser).traverse(eqTree(MyJavaMethod))

    termSelectTraverser.traverse(scalaSelect)

    outputWriter.toString shouldBe "(() -> 1).get"
  }

  test("traverse() when qualifier is a Term.Ascribe applied to a Term.Function, should wrap in parentheses") {
    val termFunction = Term.Function(Nil, Lit.Int(1))
    val ascribedTermFunction = Term.Ascribe(termFunction, t"Supplier[Int]")
    val inputTermSelect = Term.Select(ascribedTermFunction, MyMethod)
    val outputTermSelect = Term.Select(ascribedTermFunction, MyJavaMethod)

    when(termTypeInferrer.infer(eqTree(ascribedTermFunction))).thenReturn(None)
    when(termSelectTransformer.transform(eqTree(inputTermSelect), eqTermSelectContext(TermSelectContext()))).thenReturn(outputTermSelect)

    doWrite("(Supplier<Integer>)() -> 1").when(termTraverser).traverse(eqTree(ascribedTermFunction))
    doWrite("get").when(termNameTraverser).traverse(eqTree(MyJavaMethod))

    termSelectTraverser.traverse(inputTermSelect)

    outputWriter.toString shouldBe "((Supplier<Integer>)() -> 1).get"
  }

  test("traverse() when qualifier is a Term.Apply should break the line") {
    val arg = List(Term.Name("arg1"))

    val scalaQual = Term.Apply(ScalaSelectWithTermName, arg)
    val scalaSelect = Term.Select(scalaQual, MyMethod2)

    val javaQual = Term.Apply(JavaSelectWithTermName, arg)
    val javaSelect = Term.Select(javaQual, MyJavaMethod2)

    when(termTypeInferrer.infer(eqTree(scalaQual))).thenReturn(None)
    when(termSelectTransformer.transform(eqTree(scalaSelect), eqTermSelectContext(TermSelectContext()))).thenReturn(javaSelect)

    doWrite("MyJavaClass.myJavaMethod(arg1)").when(termTraverser).traverse(eqTree(javaQual))
    doWrite("myJavaMethod2").when(termNameTraverser).traverse(eqTree(MyJavaMethod2))

    termSelectTraverser.traverse(scalaSelect)

    outputWriter.toString shouldBe
    """MyJavaClass.myJavaMethod(arg1)
        |.myJavaMethod2""".stripMargin
  }
}
