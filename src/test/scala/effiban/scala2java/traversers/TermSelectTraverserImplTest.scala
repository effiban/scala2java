package effiban.scala2java.traversers

import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.testtrees.TermNames.ScalaTermName
import effiban.scala2java.transformers.ScalaToJavaTermSelectTransformer

import scala.meta.Term

class TermSelectTraverserImplTest extends UnitTestSuite {

  private val termTraverser = mock[TermTraverser]
  private val termNameTraverser = mock[TermNameTraverser]
  private val scalaToJavaTermSelectTransformer = mock[ScalaToJavaTermSelectTransformer]

  private val termSelectTraverser = new TermSelectTraverserImpl(
    termTraverser,
    termNameTraverser,
    scalaToJavaTermSelectTransformer
  )

  test("traverse() for non-'scala'") {
    val myClass = Term.Name("MyClass")
    val myMethod = Term.Name("myMethod")
    val myJavaClass = Term.Name("MyJavaClass")
    val myJavaMethod = Term.Name("myJavaMethod")
    val scalaSelect = Term.Select(qual = myClass, name = myMethod)
    val javaSelect = Term.Select(qual = myJavaClass, name = myJavaMethod)

    when(scalaToJavaTermSelectTransformer.transform(eqTree(scalaSelect))).thenReturn(javaSelect)

    doWrite("MyJavaClass").when(termTraverser).traverse(eqTree(myJavaClass))
    doWrite("myJavaMethod").when(termNameTraverser).traverse(eqTree(myJavaMethod))

    termSelectTraverser.traverse(scalaSelect)

    outputWriter.toString shouldBe "MyJavaClass.myJavaMethod"
  }

  test("traverse() for 'scala'") {
    val className = Term.Name("SomeClass")
    val select = Term.Select(ScalaTermName, className)

    doWrite("SomeClass").when(termNameTraverser).traverse(eqTree(className))

    termSelectTraverser.traverse(select)

    outputWriter.toString shouldBe "SomeClass"
  }
}
