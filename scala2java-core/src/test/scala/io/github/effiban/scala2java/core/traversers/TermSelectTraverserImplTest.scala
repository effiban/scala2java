package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.TermSelectContext
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TypeNames
import io.github.effiban.scala2java.spi.transformers.TermSelectTransformer
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Lit, Term, XtensionQuasiquoteType}

class TermSelectTraverserImplTest extends UnitTestSuite {

  private val MyClass = Term.Name("MyClass")
  private val MyMethod = Term.Name("myMethod")
  private val MyMethod2 = Term.Name("myMethod2")
  private val MyJavaClass = Term.Name("MyJavaClass")
  private val MyJavaMethod = Term.Name("myJavaMethod")
  private val MyJavaMethod2 = Term.Name("myJavaMethod2")
  private val ScalaSelectWithTermName = Term.Select(qual = MyClass, name = MyMethod)
  private val JavaSelectWithTermName = Term.Select(qual = MyJavaClass, name = MyJavaMethod)

  private val termTraverser = mock[TermTraverser]
  private val termNameTraverser = mock[TermNameTraverser]
  private val typeListTraverser = mock[TypeListTraverser]
  private val termSelectTransformer = mock[TermSelectTransformer]

  private val termSelectTraverser = new TermSelectTraverserImpl(
    termTraverser,
    termNameTraverser,
    typeListTraverser,
    termSelectTransformer
  )

  test("traverse() when qualifier is a Term.Name and has type args") {
    val typeArgs = List(TypeNames.Int)

    when(termSelectTransformer.transform(eqTree(ScalaSelectWithTermName))).thenReturn(JavaSelectWithTermName)

    doWrite("MyJavaClass").when(termTraverser).traverse(eqTree(MyJavaClass))
    doWrite("myJavaMethod").when(termNameTraverser).traverse(eqTree(MyJavaMethod))
    doWrite("<Integer>").when(typeListTraverser).traverse(eqTreeList(typeArgs))

    termSelectTraverser.traverse(ScalaSelectWithTermName, TermSelectContext(appliedTypeArgs = typeArgs))

    outputWriter.toString shouldBe "MyJavaClass.<Integer>myJavaMethod"
  }

  test("traverse() when qualifier is a Term.Name and has no type args") {
    when(termSelectTransformer.transform(eqTree(ScalaSelectWithTermName))).thenReturn(JavaSelectWithTermName)

    doWrite("MyJavaClass").when(termTraverser).traverse(eqTree(MyJavaClass))
    doWrite("myJavaMethod").when(termNameTraverser).traverse(eqTree(MyJavaMethod))

    termSelectTraverser.traverse(ScalaSelectWithTermName)

    outputWriter.toString shouldBe "MyJavaClass.myJavaMethod"
  }

  test("traverse() when qualifier is a Term.Function should wrap in parentheses") {
    val termFunction = Term.Function(Nil, Lit.Int(1))
    val scalaSelect = Term.Select(termFunction, MyMethod)
    val javaSelect = Term.Select(termFunction, MyJavaMethod)

    when(termSelectTransformer.transform(eqTree(scalaSelect))).thenReturn(javaSelect)

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

    when(termSelectTransformer.transform(eqTree(inputTermSelect))).thenReturn(outputTermSelect)

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

    when(termSelectTransformer.transform(eqTree(scalaSelect))).thenReturn(javaSelect)

    doWrite("MyJavaClass.myJavaMethod(arg1)").when(termTraverser).traverse(eqTree(javaQual))
    doWrite("myJavaMethod2").when(termNameTraverser).traverse(eqTree(MyJavaMethod2))

    termSelectTraverser.traverse(scalaSelect)

    outputWriter.toString shouldBe
    """MyJavaClass.myJavaMethod(arg1)
        |.myJavaMethod2""".stripMargin
  }
}
