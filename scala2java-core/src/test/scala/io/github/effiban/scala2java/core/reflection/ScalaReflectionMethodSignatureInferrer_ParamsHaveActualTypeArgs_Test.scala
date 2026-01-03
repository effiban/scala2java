package io.github.effiban.scala2java.core.reflection

import io.github.effiban.scala2java.core.matchers.PartialDeclDefScalatestMatcher.equalPartialDeclDef
import io.github.effiban.scala2java.core.reflection.ScalaReflectionMethodSignatureInferrer.inferPartialMethodSignature
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.entities.PartialDeclDef

import scala.meta.{Term, Type, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class ScalaReflectionMethodSignatureInferrer_ParamsHaveActualTypeArgs_Test extends UnitTestSuite {

  private val TestClassWithMethodParamActualTypeArgs = t"io.github.effiban.scala2java.core.reflection.TestClassWithMethodParamActualTypeArgs"

  private val TestCasesWhenMatches = Table(
    ("Method", "Arg Types", "Expected Param Types", "Expected Return Type"),
    (q"fun1",
      List(t"scala.collection.immutable.List[scala.Int]"),
      List(t"scala.collection.immutable.List[scala.Int]"),
      t"java.lang.String"
    ),
    (q"fun2",
      List(t"scala.collection.immutable.Map[scala.Int, scala.Long]"),
      List(t"scala.collection.immutable.Map[scala.Int, scala.Long]"),
      t"java.lang.String"
    ),
    (q"fun3",
      List(t"scala.collection.immutable.List[scala.collection.immutable.List[scala.Int]]"),
      List(t"scala.collection.immutable.List[scala.collection.immutable.List[scala.Int]]"),
      t"java.lang.String"
    ),
    (q"fun4",
      List(t"scala.collection.immutable.List[(scala.Int, scala.Long)]"),
      List(t"scala.collection.immutable.List[(scala.Int, scala.Long)]"),
      t"java.lang.String"
    ),
    (q"fun5",
      List(t"(scala.collection.immutable.List[scala.Int], scala.collection.immutable.Set[scala.Long])"),
      List(t"(scala.collection.immutable.List[scala.Int], scala.collection.immutable.Set[scala.Long])"),
      t"java.lang.String"
    ),
    (q"fun6",
      List(t"scala.collection.immutable.List[scala.Int]"),
      List(t"=> scala.collection.immutable.List[scala.Int]"),
      t"java.lang.String"
    ),
      (q"fun7",
        List(t"scala.collection.immutable.List[scala.Int]", t"scala.collection.immutable.List[scala.Int]"),
        List(t"scala.collection.immutable.List[scala.Int]*"),
        t"java.lang.String"
      )

  )

  private val TestCasesWhenDoesntMatch = Table(
    ("Method", "Arg Types"),
    (q"fun1", List(t"scala.collection.immutable.List[scala.Long]")),
    (q"fun1", List(t"scala.collection.immutable.List[scala.collection.immutable.List[scala.Int]]")),
    (q"fun2", List(t"scala.collection.immutable.Map[scala.Int, scala.Int]")),
    (q"fun2", List(t"scala.collection.immutable.Map[scala.Long, scala.Int]")),
    (q"fun2", List(t"scala.collection.immutable.Map[scala.collection.immutable.List[scala.Long, scala.Int]]")),
    (q"fun3", List(t"scala.collection.immutable.List[scala.collection.immutable.List[scala.Long]]")),
    (q"fun3", List(t"scala.collection.immutable.List[scala.Int]")),
    (q"fun4", List(t"scala.collection.immutable.List[(scala.Int, scala.Int)]")),
    (q"fun4", List(t"scala.collection.immutable.List[scala.Int]")),
    (q"fun5", List(t"(scala.collection.immutable.List[scala.Int], scala.collection.immutable.Set[scala.Int])")),
    (q"fun5", List(t"(scala.collection.immutable.List[scala.Int], scala.collection.immutable.Set[scala.Int], scala.Int)")),
    (q"fun6", List(t"scala.collection.immutable.List[scala.Long]")),
    (q"fun6", List(t"scala.collection.immutable.List[scala.collection.immutable.List[scala.Int]]")),
    (q"fun7", List(t"scala.collection.immutable.List[scala.Long]")),
    (q"fun7", List(t"scala.collection.immutable.List[scala.Int]", t"scala.collection.immutable.List[scala.Long]"))
  )

  forAll(TestCasesWhenMatches) {
    (method: Term.Name,
     argTypes: List[Type],
     expectedParamTypes: List[Type],
     expectedReturnType: Type) =>

      test(s"inferPartialMethodSignature for 'TestClassWithMethodParamActualTypeArgs.${method.toString()}(${argTypes.mkString(",")})' " +
        s"should return a signature with param type lists '${List(expectedParamTypes.map(Some(_)))}' and return type '$expectedReturnType'") {

        val result = inferPartialMethodSignature(TestClassWithMethodParamActualTypeArgs, method, List(argTypes))
        result should equalPartialDeclDef(PartialDeclDef(
          maybeParamTypeLists = List(expectedParamTypes.map(Some(_))),
          maybeReturnType = Some(expectedReturnType))
        )
      }
  }

  forAll(TestCasesWhenDoesntMatch) {
    (method: Term.Name,
     argTypes: List[Type]) =>

      test(s"inferPartialMethodSignature for 'TestClassWithMethodParamActualTypeArgs.${method.toString()}(${argTypes.mkString(",")})' " +
        " should return an empty signature due to mismatch") {
        val result = inferPartialMethodSignature(TestClassWithMethodParamActualTypeArgs, method, List(argTypes))
        result should equalPartialDeclDef(PartialDeclDef())
      }
  }
}
