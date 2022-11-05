package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.Term.This
import scala.meta.{Name, Term, Type}

class TypeSingletonToTermTransformerTest extends UnitTestSuite {

  test("transform type of 'this' should return 'this'") {
    val `this` = This(Name.Anonymous())
    val typeOfThis = Type.Singleton(`this`)

    val actualTerm = TypeSingletonToTermTransformer.transform(typeOfThis)

    actualTerm.structure shouldBe `this`.structure
  }

  test("transform type of non-'this' should return the corresponding Java type") {
    val x = Term.Name("x")
    val typeOfX = Type.Singleton(x)

    val expectedJavaType = Term.Apply(fun = Term.Select(x, Term.Name("getClass")), args = Nil)

    val actualJavaType = TypeSingletonToTermTransformer.transform(typeOfX)

    actualJavaType.structure shouldBe expectedJavaType.structure
  }
}
