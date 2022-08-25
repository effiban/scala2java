package effiban.scala2java.traversers

import effiban.scala2java.matchers.CombinedMatchers.eqTreeList
import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.testtrees.TypeNames

import scala.meta.Type

class TypeApplyTraverserImplTest extends UnitTestSuite {

  private val typeTraverser = mock[TypeTraverser]
  private val typeListTraverser = mock[TypeListTraverser]

  private val typeApplyTraverser = new TypeApplyTraverserImpl(typeTraverser, typeListTraverser)

  test("traverse") {
    val tpe = Type.Name("Map")
    val args = List(TypeNames.Int, TypeNames.String)

    val typeApply = Type.Apply(tpe = tpe, args = args)

    doWrite("Map").when(typeTraverser).traverse(eqTree(tpe))
    doWrite("<Integer, String>").when(typeListTraverser).traverse(eqTreeList(args))

    typeApplyTraverser.traverse(typeApply)

    outputWriter.toString shouldBe "Map<Integer, String>"
  }

}
