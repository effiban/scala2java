package io.github.effiban.scala2java.core.extensions

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.Scala2JavaExtension
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import java.util.ServiceLoader.Provider
import scala.meta.{Import, Importer, Source, Term, XtensionQuasiquoteImportee, XtensionQuasiquoteTerm}

class ExtensionRegistryBuilderTest extends UnitTestSuite {

  private val TheTermSelect1 = q"a.b"
  private val TheTermSelect2 = q"e.f"
  private val TheSource = Source(
    List(
      Import(
        List(
          Importer(TheTermSelect1, List(importee"c1")),
          Importer(TheTermSelect1, List(importee"c2")),
          Importer(TheTermSelect2, List(importee"g"))
        )
      )
    )
  )

  test("build() when there are two extensions and both should be applied, should return a registry with both") {
    val extensions = List(TestExtensionThatShouldBeApplied, TestExtensionThatShouldBeApplied)

    registryBuilderFrom(extensions).buildFor(TheSource) shouldBe ExtensionRegistry(extensions)
  }

  test("build() when there is are two extensions and only second should be applied, should return a registry with second only") {
    val extensions = List(TestExtensionThatShouldNotBeApplied, TestExtensionThatShouldBeApplied)

    registryBuilderFrom(extensions).buildFor(TheSource) shouldBe ExtensionRegistry(List(TestExtensionThatShouldBeApplied))
  }

  test("build() when there is one extension that should be applied should return a registry with it") {
    registryBuilderFrom(List(TestExtensionThatShouldBeApplied)).buildFor(TheSource) shouldBe ExtensionRegistry(List(TestExtensionThatShouldBeApplied))
  }

  test("build() when there is one extension that should not be applied, should return an empty registry") {
    registryBuilderFrom(List(TestExtensionThatShouldNotBeApplied)).buildFor(TheSource) shouldBe ExtensionRegistry()
  }

  test("build() when there no extensions should return an empty registry") {
    emptyRegistryBuilder().buildFor(TheSource) shouldBe ExtensionRegistry()
  }

  test("build() when there is one extension that should be applied and a qualified name appears twice - should invoke the extension only once") {
    val extension = spy(TestExtensionThatShouldBeApplied)
    registryBuilderFrom(List(extension)).buildFor(TheSource)
    verify(extension).shouldBeAppliedIfContains(eqTree(TheTermSelect1))
  }

  test("build() when there is one extension that should not be applied and a qualified name appears twice - should invoke the extension only once") {
    val extension = spy(TestExtensionThatShouldNotBeApplied)
    registryBuilderFrom(List(extension)).buildFor(TheSource)
    verify(extension).shouldBeAppliedIfContains(eqTree(TheTermSelect1))
  }

  private def emptyRegistryBuilder() = registryBuilderFrom(Nil)

  private def registryBuilderFrom(extensions: List[Scala2JavaExtension]) = new ExtensionRegistryBuilder() {

    override private[extensions] def loadExtensions() = {
      val extensionProviders = extensions.map(extension => new Provider[Scala2JavaExtension] {
        override def `type`(): Class[Scala2JavaExtension] = classOf[Scala2JavaExtension]

        override def get(): Scala2JavaExtension = extension
      })
      LazyList.from(extensionProviders)
    }
  }

  private object TestExtensionThatShouldBeApplied extends Scala2JavaExtension {
    override def shouldBeAppliedIfContains(termSelect: Term.Select): Boolean = termSelect.structure == TheTermSelect1.structure
  }

  private object TestExtensionThatShouldNotBeApplied extends Scala2JavaExtension {
    override def shouldBeAppliedIfContains(termSelect: Term.Select): Boolean =
      !Set(TheTermSelect1, TheTermSelect2).exists(_.structure == termSelect.structure)
  }
}
