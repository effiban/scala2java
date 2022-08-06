package effiban.scala2java.traversers

import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import effiban.scala2java.testsuites.UnitTestSuite
import org.mockito.ArgumentMatchers

import scala.meta.Term.New
import scala.meta.{Init, Name, Term, Type}

class NewTraverserImplTest extends UnitTestSuite {

  private val initTraverser = mock[InitTraverser]

  private val newTraverser = new NewTraverserImpl(initTraverser)

  test("traverse") {
    val init = Init(
      tpe = Type.Name("MyClass"),
      name = Name.Anonymous(),
      argss = List(List(Term.Name("val1"), Term.Name("val2")))
    )

    val `new` = New(init)

    doWrite("MyClass(val1, val2)").when(initTraverser).traverse(eqTree(init), ArgumentMatchers.eq(false))

    newTraverser.traverse(`new`)

    outputWriter.toString shouldBe "new MyClass(val1, val2)"
  }
}
