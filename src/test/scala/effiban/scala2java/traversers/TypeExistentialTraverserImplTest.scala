package effiban.scala2java.traversers

import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import effiban.scala2java.testsuites.UnitTestSuite

import scala.meta.{Decl, Type}

class TypeExistentialTraverserImplTest extends UnitTestSuite {

  private val typeTraverser = mock[TypeTraverser]

  private val typeExistentialTraverser = new TypeExistentialTraverserImpl(typeTraverser)

  test("traverse") {
    val typeA = Type.Name("A")
    val typeB = Type.Name("B")

    val stats = List(Decl.Type(mods = Nil, name = typeB, tparams = Nil, bounds = Type.Bounds(None, None)))

    val typeExistential = Type.Existential(tpe = typeA, stats = stats)

    doWrite("A").when(typeTraverser).traverse(eqTree(typeA))

    typeExistentialTraverser.traverse(typeExistential)

    outputWriter.toString shouldBe "A/* forSome List(type B) */"
  }

}
