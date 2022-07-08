package effiban.scala2java

import effiban.scala2java.matchers.TreeListMatcher.eqTreeList
import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.stubbers.OutputWriterStubber.doWrite

import scala.meta.{Term, Type}

class ApplyTypeTraverserImplTest extends UnitTestSuite {

  private val typeTraverser = mock[TypeTraverser]
  private val termTraverser = mock[TermTraverser]
  private val typeListTraverser = mock[TypeListTraverser]

  private val applyTypeTraverser = new ApplyTypeTraverserImpl(
    typeTraverser,
    termTraverser,
    typeListTraverser)

  test("traverse() when function is 'classOf' should convert to the Java equivalent") {
    val typeName = Type.Name("T")
    doWrite("T").when(typeTraverser).traverse(eqTree(typeName))

    applyTypeTraverser.traverse(Term.ApplyType(fun = Term.Name("classOf"), targs = List(typeName)))

    outputWriter.toString shouldBe "T.class"
    verifyNoMoreInteractions(termTraverser, typeListTraverser)
  }

  test("traverse() when function is regular with type args") {
    val funName = Term.Name("myFunc")
    val typeArgs = List(Type.Name("T1"), Type.Name("T2"))

    doWrite("myFunc").when(termTraverser).traverse(eqTree(funName))
    doWrite("<T1, T2>").when(typeListTraverser).traverse(eqTreeList(typeArgs))

    applyTypeTraverser.traverse(Term.ApplyType(fun = funName, targs = typeArgs))

    outputWriter.toString shouldBe "myFunc.<T1, T2>"
    verifyNoMoreInteractions(typeTraverser)
  }
}
