package io.github.effiban.scala2java.core.extensions

import io.github.effiban.scala2java.core.extensions.ForcedExtensionNamesResolver.resolve
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

class ForcedExtensionNamesResolverTest extends UnitTestSuite {

  private final val ForcedExtensionsPropertyName = "Scala2JavaForcedExtensions"

  override def beforeEach(): Unit = {
    super.beforeEach()
    clearProperty()
  }

  test("resolve() when there are two distinct names in the property without spaces should return them") {
    setProperty("Ext1,Ext2")
    resolve() shouldBe Set("Ext1", "Ext2")
  }

  test("resolve() when there are two distinct names in the property with spaces should return them without spaces") {
    setProperty(" Ext1 , Ext2 ")
    resolve() shouldBe Set("Ext1", "Ext2")
  }

  test("resolve() when there are two distinct names in the property and one empty name should skip the empty one") {
    setProperty("Ext1, ,Ext2")
    resolve() shouldBe Set("Ext1", "Ext2")
  }

  test("resolve() when there are two identical names in the property should return just one") {
    setProperty("Ext,Ext")
    resolve() shouldBe Set("Ext")
  }

  test("resolve() when there is one name in the property should return it") {
    setProperty("Ext")
    resolve() shouldBe Set("Ext")
  }

  test("resolve() when the property is empty should return empty") {
    setProperty("")
    resolve() shouldBe Set.empty
  }

  test("resolve() when the property doesn't exist should return empty") {
    resolve() shouldBe Set.empty
  }

  private def setProperty(forcedExtensionNamesStr: String): Unit = {
    System.setProperty(ForcedExtensionsPropertyName, forcedExtensionNamesStr)
  }

  private def clearProperty(): Unit = {
    System.clearProperty(ForcedExtensionsPropertyName)
  }
}
