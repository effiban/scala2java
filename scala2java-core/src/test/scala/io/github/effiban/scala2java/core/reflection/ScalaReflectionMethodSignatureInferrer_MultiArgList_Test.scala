package io.github.effiban.scala2java.core.reflection

import io.github.effiban.scala2java.core.matchers.PartialDeclDefScalatestMatcher.equalPartialDeclDef
import io.github.effiban.scala2java.core.reflection.ScalaReflectionMethodSignatureInferrer.inferPartialMethodSignature
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.entities.PartialDeclDef

import scala.meta.{Term, Type, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class ScalaReflectionMethodSignatureInferrer_MultiArgList_Test extends UnitTestSuite {

  private val TestMultiArgListClassType = t"io.github.effiban.scala2java.core.reflection.TestMultiArgListClass"

  private val TestCasesWhenMatches = Table(
    ("Method", "Arg Type Lists", "Expected Param Type Lists", "Expected Return Type"),
    (q"fun1",
      List(List(t"java.lang.String", t"scala.Int"), List(t"java.lang.String", t"scala.Long")),
      List(List(t"java.lang.String", t"scala.Int"), List(t"java.lang.String", t"scala.Long")),
      t"java.lang.String"
    ),

    (q"fun2",
      List(
        List(t"java.lang.String", t"scala.Int"),
        List(t"java.lang.String", t"scala.Long"),
        List(t"java.lang.String", t"scala.Short")
      ),
      List(
        List(t"java.lang.String", t"scala.Int"),
        List(t"java.lang.String", t"scala.Long"),
        List(t"java.lang.String", t"scala.Short")
      ),
      t"java.lang.String"
    )
  )

  private val TestCasesWhenDoesntMatch = Table(
    ("Method", "Arg Type Lists"),
    (q"fun1", List(List(t"java.lang.String", t"scala.Int"), List(t"java.lang.String", t"scala.Int"))),
    (q"fun1", List(List(t"java.lang.String", t"scala.Int"))),
    (q"fun1", List(
      List(t"java.lang.String", t"scala.Int"),
      List(t"java.lang.String", t"scala.Long"),
      List(t"java.lang.String", t"scala.Long")
    )),
    (q"fun2",
      List(
        List(t"java.lang.String", t"scala.Int"),
        List(t"java.lang.String", t"scala.Long"),
      ),
    ),
    (q"fun2",
      List(
        List(t"java.lang.String", t"scala.Int"),
        List(t"java.lang.String", t"scala.Long"),
        List(t"java.lang.String", t"scala.Long"),
      ),
    )
  )

  forAll(TestCasesWhenMatches) {
    (method: Term.Name,
     argTypeLists: List[List[Type]],
     expectedParamTypeLists: List[List[Type]],
     expectedReturnType: Type) =>

      test(s"inferPartialMethodSignature for 'TestMultiArgListClass.${method.toString()}(${argTypeLists.mkString(",")})' " +
        s"should return a signature with param type lists '$expectedParamTypeLists' and return type '$expectedReturnType'") {

        val result = inferPartialMethodSignature(TestMultiArgListClassType, method, argTypeLists)
        result should equalPartialDeclDef(PartialDeclDef(
          maybeParamTypeLists = expectedParamTypeLists.map(_.map(Some(_))),
          maybeReturnType = Some(expectedReturnType))
        )
      }
  }


  forAll(TestCasesWhenDoesntMatch) { (method: Term.Name, argTypeLists: List[List[Type]]) =>
    test(s"inferPartialMethodSignature for 'TestMultiArgListClass.${method.toString()}(${argTypeLists.mkString(",")})' " +
      " should return an empty signature due to mismatch") {
      val result = inferPartialMethodSignature(TestMultiArgListClassType, method, argTypeLists)
      result should equalPartialDeclDef(PartialDeclDef())
    }
  }
}
