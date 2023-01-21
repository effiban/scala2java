package io.github.effiban.scala2java.core.extensions

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.Scala2JavaExtension
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import org.mockito.ArgumentMatchersSugar.eqTo

import java.util.ServiceLoader.Provider
import scala.meta.{Import, Importer, Source, XtensionQuasiquoteImportee, XtensionQuasiquoteTerm}

class ExtensionRegistryBuilderTest extends UnitTestSuite {

  private final val ForcedExtensionNames = Set("Extension1", "Extension2")

  private val TermSelect1 = q"a.b"
  private val TermSelect2 = q"d.e"
  private val TermSelects = List(TermSelect1, TermSelect2)
  private val TheSource = Source(
    List(
      Import(
        List(
          Importer(TermSelect1, List(importee"c")),
          Importer(TermSelect2, List(importee"f"))
        )
      )
    )
  )

  private val forcedExtensionNamesResolver = mock[ForcedExtensionNamesResolver]
  private val extensionApplicablePredicate = mock[ExtensionApplicablePredicate]

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    when(forcedExtensionNamesResolver.resolve()).thenReturn(ForcedExtensionNames)
  }

  test("build() when there are two extensions and both should be applied - should return a registry with both") {
    val extensions = List(mock[Scala2JavaExtension], mock[Scala2JavaExtension])

    whenApplyPredicate(extensions.head).thenReturn(true)
    whenApplyPredicate(extensions(1)).thenReturn(true)

    registryBuilderFrom(extensions).buildFor(TheSource) shouldBe ExtensionRegistry(extensions)
  }

  test("build() when there are two extensions and only second should be applied, should return a registry with second only") {
    val extensions = List(mock[Scala2JavaExtension], mock[Scala2JavaExtension])

    whenApplyPredicate(extensions.head).thenReturn(false)
    whenApplyPredicate(extensions(1)).thenReturn(true)

    registryBuilderFrom(extensions).buildFor(TheSource) shouldBe ExtensionRegistry(List(extensions(1)))
  }

  test("build() when there is one extension that should be applied, should return a registry with it") {
    val extension = mock[Scala2JavaExtension]

    whenApplyPredicate(extension).thenReturn(true)

    registryBuilderFrom(List(extension)).buildFor(TheSource) shouldBe ExtensionRegistry(List(extension))
  }

  test("build() when there is one extension that should not be applied, should return an empty registry") {
    val extension = mock[Scala2JavaExtension]

    whenApplyPredicate(extension).thenReturn(false)

    registryBuilderFrom(List(extension)).buildFor(TheSource) shouldBe ExtensionRegistry()
  }

  test("build() when there no extensions should return an empty registry") {
    emptyRegistryBuilder().buildFor(TheSource) shouldBe ExtensionRegistry()
  }

  private def emptyRegistryBuilder() = registryBuilderFrom(Nil)

  private def registryBuilderFrom(extensions: List[Scala2JavaExtension]) =
    new ExtensionRegistryBuilder(forcedExtensionNamesResolver, extensionApplicablePredicate) {

    override private[extensions] def loadExtensions() = {
      val extensionProviders = extensions.map(extension => new Provider[Scala2JavaExtension] {
        override def `type`(): Class[Scala2JavaExtension] = classOf[Scala2JavaExtension]

        override def get(): Scala2JavaExtension = extension
      })
      LazyList.from(extensionProviders)
    }
  }

  private def whenApplyPredicate(extension: Scala2JavaExtension) = {
    when(extensionApplicablePredicate.apply(
      eqTo(extension),
      eqTo(ForcedExtensionNames),
      eqTreeList(TermSelects)
    ))
  }

}
