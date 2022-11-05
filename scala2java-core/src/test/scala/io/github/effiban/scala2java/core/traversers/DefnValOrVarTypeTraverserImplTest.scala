package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.StatContext
import io.github.effiban.scala2java.core.entities.JavaScope
import io.github.effiban.scala2java.core.matchers.TreeMatcher.eqTree
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TypeNames
import io.github.effiban.scala2java.core.typeinference.TermTypeInferrer

import scala.meta.{Lit, Term}

class DefnValOrVarTypeTraverserImplTest extends UnitTestSuite {

  private val LiteralInt = Lit.Int(3)
  private val NonInferrableTerm = Term.Apply(Term.Name("externalMethod"), Nil)

  private val typeTraverser = mock[TypeTraverser]
  private val termTypeInferrer = mock[TermTypeInferrer]

  private val defnValOrVarTypeTraverser = new DefnValOrVarTypeTraverserImpl(typeTraverser, termTypeInferrer)

  test("traverse when has declared type should traverse it") {
    val javaScope = JavaScope.Class

    doWrite("Int").when(typeTraverser).traverse(eqTree(TypeNames.Int))

    defnValOrVarTypeTraverser.traverse(
      maybeDeclType = Some(TypeNames.Int),
      maybeRhs = None,
      context = StatContext(javaScope)
    )

    outputWriter.toString shouldBe "Int"
  }

  test("traverse when has no declared type, has RHS, JavaScope is none, and type is inferred - should traverse it") {
    val javaScope = JavaScope.Class

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
    val javaScope = JavaScope.Class

    when(termTypeInferrer.infer(eqTree(NonInferrableTerm))).thenReturn(None)

    defnValOrVarTypeTraverser.traverse(
      maybeDeclType = None,
      maybeRhs = Some(NonInferrableTerm),
      context = StatContext(javaScope)
    )

    outputWriter.toString shouldBe "/* UnknownType */"
  }

  test("traverse when has no declared type, has no RHS, and JavaScope is none - should write 'UnknownType'") {
    val javaScope = JavaScope.Class

    defnValOrVarTypeTraverser.traverse(
      maybeDeclType = None,
      maybeRhs = None,
      context = StatContext(javaScope)
    )

    outputWriter.toString shouldBe "/* UnknownType */"
  }

  test("traverse when has no declared type and JavaScope is Block - should write 'var'") {
    val javaScope = JavaScope.Block

    defnValOrVarTypeTraverser.traverse(
      maybeDeclType = None,
      maybeRhs = None,
      context = StatContext(javaScope)
    )

    outputWriter.toString shouldBe "var"
  }
}
