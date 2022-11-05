package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.matchers.CombinedMatchers.eqOptionTreeList
import io.github.effiban.scala2java.core.matchers.TreeMatcher.eqTree
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TypeNames
import org.mockito.ArgumentMatchers.any

import scala.meta.{Lit, Term, Type}

class TryWithHandlerTypeInferrerImplTest extends UnitTestSuite {

  private val TryStatement = Term.Apply(Term.Name("doSomething"), Nil)

  private val CatchHandler = Term.Name("someCatchHandler")

  private val FinallyStatement = Term.Apply(Term.Name("cleanup"), Nil)

  private val termTypeInferrer = mock[TermTypeInferrer]
  private val collectiveTypeInferrer = mock[CollectiveTypeInferrer]

  private val tryWithHandlerTypeInferrer =  new TryWithHandlerTypeInferrerImpl(termTypeInferrer, collectiveTypeInferrer)

  test("infer when has a 'try' expression only should return the collective type of: 'try' and 2 anonymous types") {
    val tryWithHandler = Term.TryWithHandler(
      expr = TryStatement,
      catchp = Lit.Unit(),
      finallyp = None
    )

    when(termTypeInferrer.infer(eqTree(TryStatement))).thenReturn(Some(TypeNames.String))
    when(collectiveTypeInferrer.inferScalar(eqOptionTreeList(List(Some(TypeNames.String), Some(Type.AnonymousName()), Some(Type.AnonymousName())))))
      .thenReturn(Some(TypeNames.String))

    tryWithHandlerTypeInferrer.infer(tryWithHandler).value.structure shouldBe TypeNames.String.structure
  }

  test("infer when has a 'try' expression and a catch handler should return the collective type of: 'try' expr, None, and anonymous") {
    val tryWithHandler = Term.TryWithHandler(
      expr = TryStatement,
      catchp = CatchHandler,
      finallyp = None
    )

    when(termTypeInferrer.infer(eqTree(TryStatement))).thenReturn(Some(TypeNames.String))
    when(collectiveTypeInferrer.inferScalar(eqOptionTreeList(List(Some(TypeNames.String), None, Some(Type.AnonymousName())))))
      .thenReturn(None)

    tryWithHandlerTypeInferrer.infer(tryWithHandler) shouldBe None
  }

  test("infer when has 'try' and finally expressions should return the collective type of: 'try' expr, anonymous and 'finally' expr") {
    val tryWithHandler = Term.TryWithHandler(
      expr = TryStatement,
      catchp = Lit.Unit(),
      finallyp = Some(FinallyStatement)
    )

    val collectiveType = Type.Name("Any")

    when(termTypeInferrer.infer(any[Term])).thenAnswer( (term: Term) => {
      term match {
        case t if t.structure == TryStatement.structure => Some(TypeNames.String)
        case t if t.structure == FinallyStatement.structure => Some(TypeNames.Int)
      }
    })
    when(collectiveTypeInferrer.inferScalar(eqOptionTreeList(List(Some(TypeNames.String), Some(Type.AnonymousName()), Some(TypeNames.Int)))))
      .thenReturn(Some(collectiveType))

    tryWithHandlerTypeInferrer.infer(tryWithHandler).value.structure shouldBe collectiveType.structure
  }

  test("infer when has all three parts should return the collective type of: 'try' expr, None and 'finally' expr") {
    val tryWithHandler = Term.TryWithHandler(
      expr = TryStatement,
      catchp = CatchHandler,
      finallyp = Some(FinallyStatement)
    )

    when(termTypeInferrer.infer(any[Term])).thenAnswer((term: Term) => {
      term match {
        case t if t.structure == TryStatement.structure => Some(TypeNames.String)
        case t if t.structure == FinallyStatement.structure => Some(TypeNames.Int)
      }
    })
    when(collectiveTypeInferrer.inferScalar(eqOptionTreeList(List(Some(TypeNames.String), None, Some(TypeNames.Int)))))
      .thenReturn(None)

    tryWithHandlerTypeInferrer.infer(tryWithHandler) shouldBe None
  }
}
