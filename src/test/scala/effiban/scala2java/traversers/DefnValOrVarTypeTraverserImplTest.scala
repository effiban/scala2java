package effiban.scala2java.traversers

import effiban.scala2java.contexts.StatContext
import effiban.scala2java.entities.JavaTreeType
import effiban.scala2java.entities.TraversalContext.javaScope
import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.testtrees.TypeNames
import effiban.scala2java.typeinference.TermTypeInferrer

import scala.meta.{Lit, Term}

class DefnValOrVarTypeTraverserImplTest extends UnitTestSuite {

  private val LiteralInt = Lit.Int(3)
  private val NonInferrableTerm = Term.Apply(Term.Name("externalMethod"), Nil)

  private val typeTraverser = mock[TypeTraverser]
  private val termTypeInferrer = mock[TermTypeInferrer]

  private val defnValOrVarTypeTraverser = new DefnValOrVarTypeTraverserImpl(typeTraverser, termTypeInferrer)

  test("traverse when has declared type should traverse it") {
    doWrite("Int").when(typeTraverser).traverse(eqTree(TypeNames.Int))

    defnValOrVarTypeTraverser.traverse(
      maybeDeclType = Some(TypeNames.Int),
      maybeRhs = None,
      context = StatContext(javaScope)
    )

    outputWriter.toString shouldBe "Int"
  }

  test("traverse when has no declared type, has RHS, JavaScope is none, and type is inferred - should traverse it") {
    when(termTypeInferrer.infer(eqTree(LiteralInt))).thenReturn(Some(TypeNames.Int))
    doWrite("Int").when(typeTraverser).traverse(eqTree(TypeNames.Int))

    defnValOrVarTypeTraverser.traverse(
      maybeDeclType = None,
      maybeRhs = Some(LiteralInt),
      context = StatContext(javaScope)
    )

    outputWriter.toString shouldBe "Int"
  }

  test("traverse when has no declared type, has RHS, JavaScope is none, and type not inferred - should write 'UnknownType'") {
    when(termTypeInferrer.infer(eqTree(NonInferrableTerm))).thenReturn(None)

    defnValOrVarTypeTraverser.traverse(
      maybeDeclType = None,
      maybeRhs = Some(NonInferrableTerm),
      context = StatContext(javaScope)
    )

    outputWriter.toString shouldBe "/* UnknownType */"
  }

  test("traverse when has no declared type, has no RHS, and JavaScope is none - should write 'UnknownType'") {
    defnValOrVarTypeTraverser.traverse(
      maybeDeclType = None,
      maybeRhs = None,
      context = StatContext(javaScope)
    )

    outputWriter.toString shouldBe "/* UnknownType */"
  }

  test("traverse when has no declared type and JavaScope is Block - should write 'var'") {
    javaScope = JavaTreeType.Block

    defnValOrVarTypeTraverser.traverse(
      maybeDeclType = None,
      maybeRhs = None,
      context = StatContext(javaScope)
    )

    outputWriter.toString shouldBe "var"
  }
}
