package io.github.effiban.scala2java.core.reflection

import io.github.effiban.scala2java.core.entities.TypeSelects.ScalaAnyVal
import io.github.effiban.scala2java.core.matchers.PartialDeclDefScalatestMatcher.equalPartialDeclDef
import io.github.effiban.scala2java.core.reflection.ScalaReflectionMethodSignatureInferrer.inferPartialMethodSignature
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.entities.PartialDeclDef

import scala.meta.{Term, Type, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class ScalaReflectionMethodSignatureInferrer_WithArgs_Test extends UnitTestSuite {

  private val TestClassType = t"io.github.effiban.scala2java.core.reflection.TestClass"
  private val TestObject = q"io.github.effiban.scala2java.core.reflection.TestObject"

  private val TestCasesWithParentTypeWhenMatches = Table(
    ("Method", "Arg Types", "Expected Param Types", "Expected Return Type"),
    (q"fun6", List(t"scala.Int", t"scala.Long"), List(t"scala.Int", t"scala.Long"), t"java.lang.String"),
    (q"fun7", List(t"scala.Int"), List(ScalaAnyVal), t"java.lang.String"),
    (q"fun8", List(t"(scala.Int, scala.Long)"), List(t"(scala.Int, scala.Long)"), t"java.lang.String"),
    (q"fun9", List(t"() => scala.Int"), List(t"() => scala.Int"), t"scala.Int"),
    (q"fun10",
      List(t"(scala.Int, scala.Long) => java.lang.String"),
      List(t"(scala.Int, scala.Long) => java.lang.String"),
      t"java.lang.String"
    ),
    (q"fun11", List(t"scala.Int"), List(t"=> scala.Int"), t"java.lang.String"),
    (q"fun12", Nil, List(t"scala.Int*"), t"java.lang.String"),
    (q"fun12", List(t"scala.Int"), List(t"scala.Int*"), t"java.lang.String"),
    (q"fun12", List(t"scala.Int", t"scala.Int"), List(t"scala.Int*"), t"java.lang.String"),
    (q"fun13", List(t"java.lang.String"), List(t"java.lang.String", t"scala.Int*"), t"java.lang.String"),
    (q"fun13", List(t"java.lang.String", t"scala.Int"), List(t"java.lang.String", t"scala.Int*"), t"java.lang.String"),
    (q"fun13", List(t"java.lang.String", t"scala.Int", t"scala.Int"), List(t"java.lang.String", t"scala.Int*"), t"java.lang.String"),
    (q"fun14", List(t"java.lang.String", t"scala.Int"), List(t"java.lang.String", t"scala.Int"), t"java.lang.String"),
    (q"fun14", List(t"java.lang.String"), List(t"java.lang.String", t"scala.Int"), t"java.lang.String"),
    (q"fun15", Nil, List(t"java.lang.String", t"scala.Int"), t"java.lang.String"),
    (q"fun15", List(t"java.lang.String", t"scala.Int"), List(t"java.lang.String", t"scala.Int"), t"java.lang.String"),
  )

  private val TestCasesWithParentTypeWhenDoesntMatch = Table(
    ("Method", "Arg Types"),
    (q"fun6", List(t"scala.String", t"scala.Long")),
    (q"fun6", List(t"scala.Long", t"scala.String")),
    (q"fun8", List(t"(scala.Long, scala.Int)")),
    (q"fun8", List(t"(scala.Int)")),
    (q"fun8", List(t"(scala.Int, scala.Long, java.lang.String)")),
    (q"fun9", List(t"scala.Int => scala.Int")),
    (q"fun9", List(t"scala.Int")),
    (q"fun10", List(t"(scala.Long, scala.Int) => java.lang.String")),
    (q"fun10", List(t"(scala.Int, scala.Long, java.lang.String) => java.lang.String")),
    (q"fun11", List(t"() => scala.Int")),
    (q"fun11", List(t"scala.Long")),
    (q"fun12", List(t"java.lang.String")),
    (q"fun12", List(t"java.lang.String", t"scala.Int")),
    (q"fun12", List(t"scala.Int", t"java.lang.String", t"scala.Int")),
    (q"fun12", List(t"scala.Int", t"scala.Int", t"java.lang.String")),
    (q"fun13", List(t"scala.Int", t"java.lang.String")),
    (q"fun13", List(t"java.lang.String", t"java.lang.String")),
    (q"fun13", List(t"java.lang.String", t"scala.Int", t"java.lang.String")),
    (q"fun14", List(t"java.lang.String", t"scala.Int", t"scala.Int")),
    (q"fun14", List(t"scala.Int", t"java.lang.String")),
    (q"fun15", List(t"java.lang.String")),
    (q"fun15", List(t"java.lang.String", t"scala.Int", t"scala.Int"))
  )

  private val TestCasesWithoutParentTypeWhenMatches = Table(
    ("Method", "Arg Types", "Expected Param Types", "Expected Return type"),
    (q"fun6", List(t"scala.Int", t"scala.Long"), List(t"scala.Int", t"scala.Long"), t"java.lang.String")
  )

  private val TestCasesWithoutParentTypeWhenDoesntMatch = Table(
    ("Method", "Arg Types"),
    (q"fun6", Nil),
    (q"fun6", List(t"scala.String")),
    (q"fun6", List(t"scala.Int", t"scala.Long", t"scala.String"))
  )

  forAll(TestCasesWithParentTypeWhenMatches) {
    (method: Term.Name,
     argTypes: List[Type],
     expectedParamTypes: List[Type],
     expectedReturnType: Type) =>

      test(s"inferPartialMethodSignature for 'TestClass.${method.toString()}(${argTypes.mkString(",")})' " +
        s"should return a signature with param type lists '${List(expectedParamTypes.map(Some(_)))}' and return type '$expectedReturnType'") {

        val result = inferPartialMethodSignature(TestClassType, method, List(argTypes))
        result should equalPartialDeclDef(PartialDeclDef(
          maybeParamTypeLists = List(expectedParamTypes.map(Some(_))),
          maybeReturnType = Some(expectedReturnType))
        )
      }
  }

  forAll(TestCasesWithoutParentTypeWhenMatches) {
    (method: Term.Name,
     argTypes: List[Type],
     expectedParamTypes: List[Type],
     expectedReturnType: Type) =>

      test(s"inferPartialMethodSignature for 'TestObject.${method.toString()}(${argTypes.mkString(",")})' " +
        s"should return a signature with arg types '${List(expectedParamTypes.map(Some(_)))}' and return type '$expectedReturnType'") {

        val result = inferPartialMethodSignature(TestObject, method, List(argTypes))
        result should equalPartialDeclDef(PartialDeclDef(
          maybeParamTypeLists = List(expectedParamTypes.map(Some(_))),
          maybeReturnType = Some(expectedReturnType))
        )
      }
  }

  forAll(TestCasesWithParentTypeWhenDoesntMatch) { (method: Term.Name, argTypes: List[Type]) =>
    test(s"inferPartialMethodSignature for 'TestClass.${method.toString()}(${argTypes.mkString(",")})' " +
      " should return an empty signature due to mismatch") {
      val result = inferPartialMethodSignature(TestClassType, method, List(argTypes))
      result should equalPartialDeclDef(PartialDeclDef())
    }
  }

  forAll(TestCasesWithoutParentTypeWhenDoesntMatch) { (method: Term.Name, argTypes: List[Type]) =>
    test(s"inferPartialMethodSignature for 'TestObject.${method.toString()}(${argTypes.mkString(",")})' " +
      " should return an empty signature due to mismatch") {
      val result = inferPartialMethodSignature(TestObject, method, List(argTypes))
      result should equalPartialDeclDef(PartialDeclDef())
    }
  }
}
