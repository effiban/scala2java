package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.TermSelectContext
import io.github.effiban.scala2java.core.matchers.TermSelectTransformationContextMatcher.eqTermSelectTransformationContext
import io.github.effiban.scala2java.core.renderers.{TermNameRenderer, TypeListRenderer}
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TypeNames
import io.github.effiban.scala2java.core.typeinference.QualifierTypeInferrer
import io.github.effiban.scala2java.spi.contexts.TermSelectTransformationContext
import io.github.effiban.scala2java.spi.transformers.TermSelectTransformer
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Lit, Term, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class ExpressionTermSelectTraverserImplTest extends UnitTestSuite {

  private val MyInstance = Term.Name("MyObject")
  private val MyType = t"MyType"
  private val MyMethod = Term.Name("myMethod")
  private val MyMethod2 = Term.Name("myMethod2")
  private val MyJavaClass = Term.Name("MyJavaClass")
  private val MyJavaMethod = Term.Name("myJavaMethod")
  private val MyJavaMethod2 = Term.Name("myJavaMethod2")
  private val ScalaSelectWithTermName = Term.Select(qual = MyInstance, name = MyMethod)
  private val JavaSelectWithTermName = Term.Select(qual = MyJavaClass, name = MyJavaMethod)

  private val qualifierTraverser = mock[ExpressionTermTraverser]
  private val transformedTermTraverser = mock[TermTraverser]
  private val termNameRenderer = mock[TermNameRenderer]
  private val typeTraverser = mock[TypeTraverser]
  private val typeListRenderer = mock[TypeListRenderer]
  private val qualifierTypeInferrer = mock[QualifierTypeInferrer]
  private val termSelectTransformer = mock[TermSelectTransformer]

  private val termSelectTraverser = new ExpressionTermSelectTraverserImpl(
    qualifierTraverser,
    transformedTermTraverser,
    termNameRenderer,
    typeTraverser,
    typeListRenderer,
    qualifierTypeInferrer,
    termSelectTransformer
  )

  test("traverse() when qualifier is a Term.Name, and has type args and inferred qualifier type") {
    val typeArg = TypeNames.Int
    val typeArgs = List(typeArg)
    val context = TermSelectContext(appliedTypeArgs = typeArgs)
    val expectedTransformationContext = TermSelectTransformationContext(Some(MyType))

    when(qualifierTypeInferrer.infer(eqTree(ScalaSelectWithTermName))).thenReturn(Some(MyType))
    when(termSelectTransformer.transform(eqTree(ScalaSelectWithTermName), eqTermSelectTransformationContext(expectedTransformationContext)))
      .thenReturn(Some(JavaSelectWithTermName))

    doWrite("MyJavaClass").when(qualifierTraverser).traverse(eqTree(MyJavaClass))
    doWrite("myJavaMethod").when(termNameRenderer).render(eqTree(MyJavaMethod))
    doReturn(t"int").when(typeTraverser).traverse(eqTree(typeArg))
    doWrite("<Integer>").when(typeListRenderer).render(eqTreeList(List(t"int")))

    termSelectTraverser.traverse(ScalaSelectWithTermName, context)

    outputWriter.toString shouldBe "MyJavaClass.<Integer>myJavaMethod"
  }

  test("traverse() when qualifier is a Term.Name, and has type args and inferred qualifier type, and transformer returns None ") {
    val typeArg = TypeNames.Int
    val typeArgs = List(typeArg)
    val context = TermSelectContext(appliedTypeArgs = typeArgs)
    val expectedTransformationContext = TermSelectTransformationContext(Some(MyType))

    when(qualifierTypeInferrer.infer(eqTree(ScalaSelectWithTermName))).thenReturn(Some(MyType))
    when(termSelectTransformer.transform(eqTree(ScalaSelectWithTermName), eqTermSelectTransformationContext(expectedTransformationContext)))
      .thenReturn(None)

    doWrite("MyInstance").when(qualifierTraverser).traverse(eqTree(MyInstance))
    doWrite("myMethod").when(termNameRenderer).render(eqTree(MyMethod))
    doReturn(t"int").when(typeTraverser).traverse(eqTree(typeArg))
    doWrite("<Integer>").when(typeListRenderer).render(eqTreeList(List(t"int")))

    termSelectTraverser.traverse(ScalaSelectWithTermName, context)

    outputWriter.toString shouldBe "MyInstance.<Integer>myMethod"
  }

  test("traverse() when qualifier is a Term.Name, and has type args but no inferred qualifier type") {
    val typeArg = TypeNames.Int
    val typeArgs = List(TypeNames.Int)
    val context = TermSelectContext(appliedTypeArgs = typeArgs)
    val expectedTransformationContext = TermSelectTransformationContext()

    when(qualifierTypeInferrer.infer(eqTree(ScalaSelectWithTermName))).thenReturn(None)
    when(termSelectTransformer.transform(eqTree(ScalaSelectWithTermName), eqTermSelectTransformationContext(expectedTransformationContext)))
      .thenReturn(Some(JavaSelectWithTermName))

    doWrite("MyJavaClass").when(qualifierTraverser).traverse(eqTree(MyJavaClass))
    doWrite("myJavaMethod").when(termNameRenderer).render(eqTree(MyJavaMethod))
    doReturn(t"int").when(typeTraverser).traverse(eqTree(typeArg))
    doWrite("<Integer>").when(typeListRenderer).render(eqTreeList(List(t"int")))

    termSelectTraverser.traverse(ScalaSelectWithTermName, context)

    outputWriter.toString shouldBe "MyJavaClass.<Integer>myJavaMethod"
  }

  test("traverse() when qualifier is a Term.Name and has no type args and no inferred qualifier type") {
    when(qualifierTypeInferrer.infer(eqTree(ScalaSelectWithTermName))).thenReturn(None)
    when(termSelectTransformer.transform(eqTree(ScalaSelectWithTermName), eqTermSelectTransformationContext(TermSelectTransformationContext())))
      .thenReturn(Some(JavaSelectWithTermName))

    doWrite("MyJavaClass").when(qualifierTraverser).traverse(eqTree(MyJavaClass))
    doWrite("myJavaMethod").when(termNameRenderer).render(eqTree(MyJavaMethod))
    termSelectTraverser.traverse(ScalaSelectWithTermName)

    outputWriter.toString shouldBe "MyJavaClass.myJavaMethod"
  }

  test("traverse() when qualifier is a Term.Function should wrap in parentheses") {
    val termFunction = Term.Function(Nil, Lit.Int(1))
    val scalaSelect = Term.Select(termFunction, MyMethod)
    val javaSelect = Term.Select(termFunction, MyJavaMethod)

    when(qualifierTypeInferrer.infer(eqTree(scalaSelect))).thenReturn(None)
    when(termSelectTransformer.transform(eqTree(scalaSelect), eqTermSelectTransformationContext(TermSelectTransformationContext())))
      .thenReturn(Some(javaSelect))

    doWrite("() -> 1").when(qualifierTraverser).traverse(eqTree(termFunction))
    doWrite("get").when(termNameRenderer).render(eqTree(MyJavaMethod))
    termSelectTraverser.traverse(scalaSelect)

    outputWriter.toString shouldBe "(() -> 1).get"
  }

  test("traverse() when qualifier is a Term.Ascribe applied to a Term.Function, should wrap in parentheses") {
    val termFunction = Term.Function(Nil, Lit.Int(1))
    val ascribedTermFunction = Term.Ascribe(termFunction, t"Supplier[Int]")
    val inputTermSelect = Term.Select(ascribedTermFunction, MyMethod)
    val outputTermSelect = Term.Select(ascribedTermFunction, MyJavaMethod)

    when(qualifierTypeInferrer.infer(eqTree(inputTermSelect))).thenReturn(None)
    when(termSelectTransformer.transform(eqTree(inputTermSelect), eqTermSelectTransformationContext(TermSelectTransformationContext())))
      .thenReturn(Some(outputTermSelect))

    doWrite("(Supplier<Integer>)() -> 1").when(qualifierTraverser).traverse(eqTree(ascribedTermFunction))
    doWrite("get").when(termNameRenderer).render(eqTree(MyJavaMethod))

    termSelectTraverser.traverse(inputTermSelect)

    outputWriter.toString shouldBe "((Supplier<Integer>)() -> 1).get"
  }

  test("traverse() when qualifier is a Term.Apply should break the line") {
    val arg = List(Term.Name("arg1"))

    val scalaQual = Term.Apply(ScalaSelectWithTermName, arg)
    val scalaSelect = Term.Select(scalaQual, MyMethod2)

    val javaQual = Term.Apply(JavaSelectWithTermName, arg)
    val javaSelect = Term.Select(javaQual, MyJavaMethod2)

    when(qualifierTypeInferrer.infer(eqTree(scalaSelect))).thenReturn(None)
    when(termSelectTransformer.transform(eqTree(scalaSelect), eqTermSelectTransformationContext(TermSelectTransformationContext())))
      .thenReturn(Some(javaSelect))

    doWrite("MyJavaClass.myJavaMethod(arg1)").when(qualifierTraverser).traverse(eqTree(javaQual))
    doWrite("myJavaMethod2").when(termNameRenderer).render(eqTree(MyJavaMethod2))
    termSelectTraverser.traverse(scalaSelect)

    outputWriter.toString shouldBe
    """MyJavaClass.myJavaMethod(arg1)
        |.myJavaMethod2""".stripMargin
  }

  test("traverse() when transformer returns a term which is not a Term.Select") {
    val typeArgs = List(TypeNames.Int)
    val context = TermSelectContext(appliedTypeArgs = typeArgs)
    val expectedTransformationContext = TermSelectTransformationContext(Some(MyType))
    val expectedTerm = q"foo(123)"

    when(qualifierTypeInferrer.infer(eqTree(ScalaSelectWithTermName))).thenReturn(Some(MyType))
    when(termSelectTransformer.transform(eqTree(ScalaSelectWithTermName), eqTermSelectTransformationContext(expectedTransformationContext)))
      .thenReturn(Some(expectedTerm))

    termSelectTraverser.traverse(ScalaSelectWithTermName, context)

    verify(transformedTermTraverser).traverse(eqTree(expectedTerm))
  }
}
