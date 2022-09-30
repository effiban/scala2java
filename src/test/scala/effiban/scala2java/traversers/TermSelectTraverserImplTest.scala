package effiban.scala2java.traversers

import effiban.scala2java.contexts.TermSelectContext
import effiban.scala2java.matchers.CombinedMatchers.eqTreeList
import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.testtrees.TypeNames
import effiban.scala2java.transformers.TermSelectTransformer

import scala.meta.{Lit, Term}

class TermSelectTraverserImplTest extends UnitTestSuite {

  private val MyClass = Term.Name("MyClass")
  private val MyMethod = Term.Name("myMethod")
  private val MyJavaClass = Term.Name("MyJavaClass")
  private val MyJavaMethod = Term.Name("myJavaMethod")
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
}
