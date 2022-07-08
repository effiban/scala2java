package effiban.scala2java.traversers

import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.testtrees.TypeNames

import scala.meta.{Decl, Term, Type}

class TypeRefineTraverserImplTest extends UnitTestSuite {

  private val Statements = List(
    Decl.Def(
      mods = Nil,
      name = Term.Name("fun"),
      tparams = Nil,
      paramss = List(List(Term.Param(mods = Nil, name = Term.Name("param"), decltpe = None, default = None))),
      decltpe = TypeNames.Unit
    )
  )

  private val typeTraverser = mock[TypeTraverser]

  private val typeRefineTraverser = new TypeRefineTraverserImpl(typeTraverser)

  test("traverse when has type + stats") {
    val tpe = Type.Name("A")
    val refinedType = Type.Refine(tpe = Some(tpe), stats = Statements)

    doWrite("A").when(typeTraverser).traverse(eqTree(tpe))

    typeRefineTraverser.traverse(refinedType)

    outputWriter.toString shouldBe "A/* List(def fun(param): Unit) */"
  }

  test("traverse when has stats only") {
    val refinedType = Type.Refine(tpe = None, stats = Statements)

    typeRefineTraverser.traverse(refinedType)

    outputWriter.toString shouldBe "/* List(def fun(param): Unit) */"
  }
}