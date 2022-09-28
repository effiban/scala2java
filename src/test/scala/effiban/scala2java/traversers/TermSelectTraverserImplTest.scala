package effiban.scala2java.traversers

import effiban.scala2java.contexts.TermSelectContext
import effiban.scala2java.matchers.CombinedMatchers.eqTreeList
import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.testtrees.TypeNames
import effiban.scala2java.transformers.ScalaToJavaTermSelectTransformer

import scala.meta.Term

class TermSelectTraverserImplTest extends UnitTestSuite {

  private val MyClass = Term.Name("MyClass")
  private val MyMethod = Term.Name("myMethod")
  private val MyJavaClass = Term.Name("MyJavaClass")
  private val MyJavaMethod = Term.Name("myJavaMethod")
  private val ScalaSelect = Term.Select(qual = MyClass, name = MyMethod)
  private val JavaSelect = Term.Select(qual = MyJavaClass, name = MyJavaMethod)

  private val termTraverser = mock[TermTraverser]
  private val termNameTraverser = mock[TermNameTraverser]
  private val typeListTraverser = mock[TypeListTraverser]
  private val scalaToJavaTermSelectTransformer = mock[ScalaToJavaTermSelectTransformer]

  private val termSelectTraverser = new TermSelectTraverserImpl(
    termTraverser,
    termNameTraverser,
    typeListTraverser,
    scalaToJavaTermSelectTransformer
  )

  test("traverse() with type args") {
    val typeArgs = List(TypeNames.Int)

    when(scalaToJavaTermSelectTransformer.transform(eqTree(ScalaSelect))).thenReturn(JavaSelect)

    doWrite("MyJavaClass").when(termTraverser).traverse(eqTree(MyJavaClass))
    doWrite("myJavaMethod").when(termNameTraverser).traverse(eqTree(MyJavaMethod))
    doWrite("<Integer>").when(typeListTraverser).traverse(eqTreeList(typeArgs))

    termSelectTraverser.traverse(ScalaSelect, TermSelectContext(appliedTypeArgs = typeArgs))

    outputWriter.toString shouldBe "MyJavaClass.<Integer>myJavaMethod"
  }

  test("traverse() without type args") {
    when(scalaToJavaTermSelectTransformer.transform(eqTree(ScalaSelect))).thenReturn(JavaSelect)

    doWrite("MyJavaClass").when(termTraverser).traverse(eqTree(MyJavaClass))
    doWrite("myJavaMethod").when(termNameTraverser).traverse(eqTree(MyJavaMethod))

    termSelectTraverser.traverse(ScalaSelect)

    outputWriter.toString shouldBe "MyJavaClass.myJavaMethod"
  }
}
