package effiban.scala2java.traversers

import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.testtrees.TermNames.ScalaTermName

import scala.meta.Term

class TermSelectTraverserImplTest extends UnitTestSuite {

  private val termTraverser = mock[TermTraverser]
  private val termNameTraverser = mock[TermNameTraverser]

  private val termSelectTraverser = new TermSelectTraverserImpl(
    termTraverser,
    termNameTraverser
  )

  test("traverse() for one level non-'scala'") {
    val myClass = Term.Name("MyClass")
    val myMethod = Term.Name("myMethod")
    val select = Term.Select(qual = myClass, name = myMethod)

    doWrite("MyClass").when(termTraverser).traverse(eqTree(myClass))
    doWrite("myMethod").when(termNameTraverser).traverse(eqTree(myMethod))

    termSelectTraverser.traverse(select)

    outputWriter.toString shouldBe "MyClass.myMethod"
  }

  test("traverse() for one level with 'scala'") {
    val className = Term.Name("SomeClass")
    val select = Term.Select(ScalaTermName, className)

    doWrite("SomeClass").when(termNameTraverser).traverse(eqTree(className))

    termSelectTraverser.traverse(select)

    outputWriter.toString shouldBe "SomeClass"
  }

  test("traverse() for two levels non-'scala'") {
    val myPackage = Term.Name("mypackage")
    val myClass = Term.Name("MyClass")
    val myMethod = Term.Name("myMethod")

    val selectLevel1 = Term.Select(myPackage, myClass)
    val selectFull = Term.Select(selectLevel1, myMethod)

    doWrite("mypackage.MyClass").when(termTraverser).traverse(eqTree(selectLevel1))
    doWrite("myMethod").when(termNameTraverser).traverse(eqTree(myMethod))

    termSelectTraverser.traverse(selectFull)

    outputWriter.toString shouldBe "mypackage.MyClass.myMethod"
  }

  test("traverse() for two levels with 'scala' at top level") {
    val someClass = Term.Name("SomeClass")
    val someMethod = Term.Name("someMethod")

    val selectLevel1 = Term.Select(ScalaTermName, someClass)
    val selectFull = Term.Select(selectLevel1, someMethod)

    doWrite("SomeClass").when(termTraverser).traverse(eqTree(selectLevel1))
    doWrite("someMethod").when(termNameTraverser).traverse(eqTree(someMethod))

    termSelectTraverser.traverse(selectFull)

    outputWriter.toString shouldBe "SomeClass.someMethod"
  }
}
