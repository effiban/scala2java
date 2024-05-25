package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.{Lit, Term, XtensionQuasiquoteTerm}

class TermTupleToTermApplyTransformerTest extends UnitTestSuite {

  private val treeTransformer = mock[TreeTransformer]

  private val termTupleToTermApplyTransformer = new TermTupleToTermApplyTransformerImpl(treeTransformer)

  test("transform when 2 args should return java.util.Map.entry(....)") {
    val args = List(q"1", q"2")
    val transformedArgs = List(q"11", q"22")
    val expectedTermApply = Term.Apply(q"java.util.Map.entry", transformedArgs)

    doAnswer((arg: Term) => arg match {
      case q"1" => q"11"
      case q"2" => q"22"
      case other => other
    }).when(treeTransformer).transform(any[Term])

    termTupleToTermApplyTransformer.transform(Term.Tuple(args)).structure shouldBe expectedTermApply.structure
  }

  test("transform when 3 args should return org.jooq.lambda.tuple.Tuple.tuple(....)") {
    val args = List(q"1", q"2", q"3")
    val transformedArgs = List(q"11", q"22", q"33")
    val expectedTermApply = Term.Apply(q"org.jooq.lambda.tuple.Tuple.tuple", transformedArgs)

    doAnswer((arg: Term) => arg match {
      case q"1" => q"11"
      case q"2" => q"22"
      case q"3" => q"33"
      case other => other
    }).when(treeTransformer).transform(any[Term])

    termTupleToTermApplyTransformer.transform(Term.Tuple(args)).structure shouldBe expectedTermApply.structure
  }

  test("transform when 4 args should return org.jooq.lambda.tuple.Tuple.tuple(....)") {
    val args = List(q"1", q"2", q"3", q"4")
    val transformedArgs = List(q"11", q"22", q"33", q"44")
    val expectedTermApply = Term.Apply(q"org.jooq.lambda.tuple.Tuple.tuple", transformedArgs)

    doAnswer((arg: Term) => arg match {
      case q"1" => q"11"
      case q"2" => q"22"
      case q"3" => q"33"
      case q"4" => q"44"
      case other => other
    }).when(treeTransformer).transform(any[Term])

    termTupleToTermApplyTransformer.transform(Term.Tuple(args)).structure shouldBe expectedTermApply.structure
  }
}
