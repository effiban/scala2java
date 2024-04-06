package io.github.effiban.scala2java.core.reflection

import io.github.effiban.scala2java.core.reflection.JavaReflectionUtils.{classForName, staticFieldFor, staticMethodFor}
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

class JavaReflectionUtilsTest extends UnitTestSuite {

  test("classForName() when valid and not in default package should return 'Success' with the class") {
    classForName("java.util.List").value shouldBe classOf[java.util.List[_]]
  }

  test("classForName() when valid and in default package should return 'Success' with the class") {
    classForName("Boolean").value shouldBe classOf[java.lang.Boolean]
  }

  test("classForName() when invalid should return None") {
    classForName("aaa.bbb.ccc") shouldBe None
  }

  test("staticFieldFor() when field exists and is static should return it") {
    staticFieldFor(classOf[java.lang.System], "out").value shouldBe classOf[java.lang.System].getField("out")
  }

  test("staticFieldFor() when field exists and is not static should return None") {
    staticFieldFor(classOf[java.lang.String], "value") shouldBe None
  }

  test("staticFieldFor() when field is invalid should return None") {
    staticFieldFor(classOf[java.lang.String], "bla") shouldBe None
  }

  test("staticMethodFor() when method exists and is static with correct number of params should return it") {
    val listOfMethod = staticMethodFor(classOf[java.util.List[_]], "of", 1).value
    listOfMethod.getName shouldBe "of"
    listOfMethod.getParameterCount shouldBe 1
    listOfMethod.getReturnType shouldBe classOf[java.util.List[_]]
  }

  test("staticMethodFor() when method exists and is static but number of params is wrong should return None") {
    staticMethodFor(classOf[java.util.List[_]], "of", 20) shouldBe None
  }

  test("staticMethodFor() when method exists and is not static should return None") {
    staticMethodFor(classOf[java.util.List[_]], "size", 0) shouldBe None
  }

  test("staticMethodFor() when method name is invalid should return None") {
    staticMethodFor(classOf[java.util.List[_]], "bla", 0) shouldBe None
  }
}
