package io.github.effiban.scala2java.core.reflection

import io.github.effiban.scala2java.core.reflection.JavaReflectionUtils.classForName
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

class JavaReflectionUtilsTest extends UnitTestSuite {

  test("classForName() when valid and not in default package should return 'Success' with the class") {
    classForName("java.util.List").success.value shouldBe classOf[java.util.List[_]]
  }

  test("classForName() when valid and in default package should return 'Success' with the class") {
    classForName("Boolean").success.value shouldBe classOf[java.lang.Boolean]
  }

  test("classForName() when invalid should fail with a ClassNotFoundException") {
    classForName("aaa.bbb.ccc").failure.exception shouldBe a[ClassNotFoundException]
  }
}
