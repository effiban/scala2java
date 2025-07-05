package io.github.effiban.scala2java.core.reflection

import io.github.effiban.scala2java.core.reflection.ScalaReflectionClassifier.isNonTrivialEmptyType
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.XtensionQuasiquoteType

class ScalaReflectionClassifierTest extends UnitTestSuite {

  test("isNonTrivialEmptyType() for a type which exists and is empty, should return true") {
    isNonTrivialEmptyType(t"io.github.effiban.scala2java.core.reflection.ScalaReflectionClassifierTest#EmptyTrait") shouldBe true
  }

  test("isNonTrivialEmptyType() for a type which has a non-trivial parent and nothing else, should return false") {
    isNonTrivialEmptyType(t"io.github.effiban.scala2java.core.reflection.ScalaReflectionClassifierTest#ClassWithNonTrivialParentOnly") shouldBe false
  }

  test("isNonTrivialEmptyType() for a type which has a non-default constructor and nothing else, should return false") {
    isNonTrivialEmptyType(t"io.github.effiban.scala2java.core.reflection.ScalaReflectionClassifierTest#ClassWithNonDefaultCtorOnly") shouldBe false
  }

  test("isNonTrivialEmptyType() for a type which has no parents and default ctor. but has data members, should return false") {
    isNonTrivialEmptyType(t"io.github.effiban.scala2java.core.reflection.ScalaReflectionClassifierTest#ClassWithDataMembersOnly") shouldBe false
  }

  test("isNonTrivialEmptyType() for a type which has no parents and default ctor. but has methods, should return false") {
    isNonTrivialEmptyType(t"io.github.effiban.scala2java.core.reflection.ScalaReflectionClassifierTest#ClassWithMethodsOnly") shouldBe false
  }

  test("isNonTrivialEmptyType() for a type which has a constructor and members, should return false") {
    isNonTrivialEmptyType(t"scala.concurrent.duration.FiniteDuration") shouldBe false
  }

  test("isNonTrivialEmptyType() for 'Serializable', should return false") {
    isNonTrivialEmptyType(t"java.io.Serializable") shouldBe false
  }

  test("isNonTrivialEmptyType() for a type which is not a class', should return false") {
    isNonTrivialEmptyType(t"A") shouldBe false
  }

  trait EmptyTrait

  class ClassWithNonTrivialParentOnly extends EmptyTrait

  class ClassWithNonDefaultCtorOnly(x: Int)

  class ClassWithDataMembersOnly {
    val x: Int = 3
    val y: List[Long] = List(3, 4)
    val z: (Int, String) = (3, "three")
    val w: (Int, Long, String) => String = (a:Int, b:Long, c:String) => (a + b).toString + c
  }

  class ClassWithMethodsOnly {
    def foo(x: Int): Int = x + 1
  }

  object ObjectWithDataMembersOnly {
    val x: Int = 3
  }
}
