package io.github.effiban.scala2java.core.extensions

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.Scala2JavaExtension

import java.util.ServiceLoader.Provider
import scala.meta.{Source, Term}

class ExtensionRegistryBuilderTest extends UnitTestSuite {

  private val TheSource = Source(List(Term.Apply(Term.Name("fun"), List(Term.Name("arg")))))

  test("build() when there are two extensions and both should be applied, should return a registry with both") {
    val extension1 = extensionThatShouldBeApplied()
    val extension2 = extensionThatShouldBeApplied()
    val extensions = List(extension1, extension2)

    registryBuilderFrom(extensions).buildFor(TheSource) shouldBe ExtensionRegistry(extensions)
  }

  test("build() when there is are two extensions and only second should be applied, should return a registry with second only") {
    val extension1 = extensionThatShouldNotBeApplied()
    val extension2 = extensionThatShouldBeApplied()
    val extensions = List(extension1, extension2)

    registryBuilderFrom(extensions).buildFor(TheSource) shouldBe ExtensionRegistry(List(extension2))
  }

  test("build() when there is one extension that should be applied should return a registry with it") {
    val extension = extensionThatShouldBeApplied()
    registryBuilderFrom(List(extension)).buildFor(TheSource) shouldBe ExtensionRegistry(List(extension))
  }

  test("build() when there is one extension that should not be applied, should return an empty registry") {
    val extension = extensionThatShouldNotBeApplied()
    registryBuilderFrom(List(extension)).buildFor(TheSource) shouldBe ExtensionRegistry()
  }

  test("build() when there no extensions should return an empty registry") {
    emptyRegistryBuilder().buildFor(TheSource) shouldBe ExtensionRegistry()
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

  private def extensionThatShouldBeApplied() = new Scala2JavaExtension() {
    override def shouldBeAppliedTo(source: Source): Boolean = source.structure == TheSource.structure
  }

  private def extensionThatShouldNotBeApplied() = new Scala2JavaExtension() {
    override def shouldBeAppliedTo(source: Source): Boolean = source.structure != TheSource.structure
  }
}