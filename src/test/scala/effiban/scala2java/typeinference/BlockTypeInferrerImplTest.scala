package effiban.scala2java.typeinference

import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.testtrees.TypeNames

import scala.meta.Term.Block
import scala.meta.{Import, Importee, Importer, Name, Term}

class BlockTypeInferrerImplTest extends UnitTestSuite {

  private val Term1 = Term.Name("x")
  private val Term2 = Term.Name("y")

  private val NonTerm = Import(
    List(
      Importer(
        ref = Term.Name("mypackage"),
        importees = List(Importee.Name(Name.Indeterminate("MyClass")))
      )
    )
  )


  private val termTypeInferrer = mock[TermTypeInferrer]

  private val blockTypeInferrer = new BlockTypeInferrerImpl(termTypeInferrer)

  test("infer when has one Term and termTypeInferrer returns a type should return it") {
    when(termTypeInferrer.infer(eqTree(Term1))).thenReturn(Some(TypeNames.Int))

    blockTypeInferrer.infer(Block(List(Term1))).value.structure shouldBe TypeNames.Int.structure
  }

  test("infer when has one Term and termTypeInferrer does not return a type should return None") {
    when(termTypeInferrer.infer(eqTree(Term1))).thenReturn(None)

    blockTypeInferrer.infer(Block(List(Term1))) shouldBe None
  }

  test("infer when has two Terms should call termTypeInferrer on the second") {
    when(termTypeInferrer.infer(eqTree(Term2))).thenReturn(Some(TypeNames.Int))

    blockTypeInferrer.infer(Block(List(Term1, Term2))).value.structure shouldBe TypeNames.Int.structure

    verify(termTypeInferrer).infer(eqTree(Term2))
    verifyNoMoreInteractions(termTypeInferrer)
  }

  test("infer when has one Term followed by one non-Term should return None") {
    blockTypeInferrer.infer(Block(List(Term1, NonTerm))) shouldBe None

    verifyNoMoreInteractions(termTypeInferrer)
  }

  test("infer when block is empty should return None") {
    blockTypeInferrer.infer(Block(Nil)) shouldBe None
  }

}
