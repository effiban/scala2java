package io.github.effiban.scala2java.core.reflection

import io.github.effiban.scala2java.core.matchers.PartialDeclDefScalatestMatcher.equalPartialDeclDef
import io.github.effiban.scala2java.core.reflection.ScalaReflectionMethodSignatureInferrer.inferPartialMethodSignature
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.entities.PartialDeclDef

import scala.meta.{Term, Type, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class ScalaReflectionMethodSignatureInferrer_Inheritance_Test extends UnitTestSuite {

  private val TestChildClassType = t"io.github.effiban.scala2java.core.reflection.TestChildClass"
  private val TestChildObject = q"io.github.effiban.scala2java.core.reflection.TestChildObject"

  private val TestChildClassCases = Table(
    ("Input method", "Expected Param Type Lists", "Expected return type"),
    (q"fun1", Nil, t"scala.Int"),
    (q"fun2", List(Nil), t"scala.Int"),
    (q"fun3", List(Nil), t"(scala.Int, java.lang.String)"),
    (q"fun4", List(Nil), t"((scala.Int, scala.Long, java.lang.String) => java.lang.String)"),
    (q"fun5", List(Nil), t"scala.collection.immutable.List[scala.Int]")
  )

  private val TestChildObjectCases = Table(
    ("Input method", "Expected Param Type Lists", "Expected return type"),
    (q"fun1", Nil, t"scala.Int"),
    (q"fun2", List(Nil), t"scala.Int"),
    (q"fun3", List(Nil), t"(scala.Int, java.lang.String)"),
    (q"fun4", List(Nil), t"((scala.Int, scala.Long, java.lang.String) => java.lang.String)"),
    (q"fun5", List(Nil), t"scala.collection.immutable.List[scala.Int]")
  )

  forAll(TestChildClassCases) { (fun: Term.Name, expectedParamTypeLists: List[List[Type]], expectedReturnType: Type) =>
    test(s"inferPartialMethodSignature for 'TestChildClass.${fun.toString()}' should return a signature with return type '$expectedReturnType'") {
      val result = inferPartialMethodSignature(TestChildClassType, fun, Nil)
      result should equalPartialDeclDef(PartialDeclDef(
        maybeParamTypeLists = expectedParamTypeLists.map(_.map(Some(_))),
        maybeReturnType = Some(expectedReturnType)
      ))
    }
  }

  forAll(TestChildObjectCases) { (fun: Term.Name, expectedParamTypeLists: List[List[Type]], expectedReturnType: Type) =>
    test(s"inferPartialMethodSignature for 'TestChildObject.${fun.toString()}' should return a signature with return type '$expectedReturnType'") {
      val result = inferPartialMethodSignature(TestChildObject, fun, Nil)
      result should equalPartialDeclDef(PartialDeclDef(
        maybeParamTypeLists = expectedParamTypeLists.map(_.map(Some(_))),
        maybeReturnType = Some(expectedReturnType)
      ))
    }
  }
}
