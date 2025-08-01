package io.github.effiban.scala2java.core.reflection

import io.github.effiban.scala2java.core.matchers.PartialDeclDefScalatestMatcher.equalPartialDeclDef
import io.github.effiban.scala2java.core.reflection.ScalaReflectionMethodSignatureInferrer.inferPartialMethodSignature
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.entities.PartialDeclDef

import scala.meta.{Term, Type, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class ScalaReflectionMethodSignatureInferrer_NoArgs_NoTypeArgsTest extends UnitTestSuite {

  private val TestClassType = t"io.github.effiban.scala2java.core.reflection.TestClass"
  private val TestObject = q"io.github.effiban.scala2java.core.reflection.TestObject"

  private val TestCasesWithParentType = Table(
    ("Input method", "Expected return type"),
    (q"fun1", t"scala.Int"),
    (q"fun2", t"scala.Int"),
    (q"fun3", t"(scala.Int, java.lang.String)"),
    (q"fun4", t"((scala.Int, scala.Long, java.lang.String) => java.lang.String)"),
    (q"fun5", t"scala.collection.immutable.List[scala.Int]")
  )

  private val TestCasesWithoutParentType = Table(
    ("Input method", "Expected return type"),
    (q"fun1", t"scala.Int"),
    (q"fun2", t"scala.Int"),
    (q"fun3", t"(scala.Int, java.lang.String)"),
    (q"fun4", t"((scala.Int, scala.Long, java.lang.String) => java.lang.String)"),
    (q"fun5", t"scala.collection.immutable.List[scala.Int]")
  )

  forAll(TestCasesWithParentType) { (fun: Term.Name, expectedReturnType: Type) =>
    test(s"inferPartialMethodSignature for 'TestClass.${fun.toString()}' should return a signature with return type '$expectedReturnType'") {
      val result = inferPartialMethodSignature(TestClassType, fun, Nil)
      result should equalPartialDeclDef(PartialDeclDef(maybeReturnType = Some(expectedReturnType)))
    }
  }

  forAll(TestCasesWithoutParentType) { (fun: Term.Name, expectedReturnType: Type) =>
    test(s"inferPartialMethodSignature for 'TestObject.${fun.toString()}' should return a signature with return type '$expectedReturnType'") {
      val result = inferPartialMethodSignature(TestObject, fun, Nil)
      result should equalPartialDeclDef(PartialDeclDef(maybeReturnType = Some(expectedReturnType)))
    }
  }
}
