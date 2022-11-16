package io.github.effiban.scala2java.core.predicates

import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.predicates.TemplateInitExcludedPredicate
import org.mockito.ArgumentMatchers.any

import scala.meta.{Init, Name, Type}

class CompositeTemplateInitExcludedPredicateTest extends UnitTestSuite {

  private val IncludedTemplateInit1 = initOf("A")
  private val IncludedTemplateInit2 = initOf("B")
  private val ExcludedCoreTemplateInit1 = initOf("ExcludedCore1")
  private val ExcludedCoreTemplateInit2 = initOf("ExcludedCore2")
  private val ExcludedLibraryTemplateInit1A = initOf("ExcludedLib1A")
  private val ExcludedLibraryTemplateInit1B = initOf("ExcludedLib1B")
  private val ExcludedLibraryTemplateInit2 = initOf("ExcludedLib2")

  private val TemplateInitExcludedScenarios = Table(
    ("Desc", "TemplateInit", "ExpectedResult"),
    ("IncludedTemplateInit1", IncludedTemplateInit1, true),
    ("IncludedTemplateInit2", IncludedTemplateInit2, true),
    ("ExcludedCoreTemplateInit1", ExcludedCoreTemplateInit1, false),
    ("ExcludedCoreTemplateInit2", ExcludedCoreTemplateInit2, false),
    ("ExcludedLibraryTemplateInit1A", ExcludedLibraryTemplateInit1A, false),
    ("ExcludedLibraryTemplateInit1B", ExcludedLibraryTemplateInit1B, false),
    ("ExcludedLibraryTemplateInit2", ExcludedLibraryTemplateInit2, false)
  )

  private val corePredicate = mock[TemplateInitExcludedPredicate]
  private val extensionPredicate1 = mock[TemplateInitExcludedPredicate]
  private val extensionPredicate2 = mock[TemplateInitExcludedPredicate]

  private implicit val extensionRegistry: ExtensionRegistry = mock[ExtensionRegistry]

  private val compositePredicate = new CompositeTemplateInitExcludedPredicate(corePredicate)

  override def beforeEach(): Unit = {
    when(extensionRegistry.templateInitExcludedPredicates).thenReturn(List(extensionPredicate1, extensionPredicate2))

    when(corePredicate.apply(any[Init])).thenAnswer( (templateInit: Init) => templateInit match {
      case aTemplateInit if aTemplateInit.structure == ExcludedCoreTemplateInit1.structure => false
      case aTemplateInit if aTemplateInit.structure == ExcludedCoreTemplateInit2.structure => false
      case _ => true
    })

    when(extensionPredicate1.apply(any[Init])).thenAnswer((templateInit: Init) => templateInit match {
      case aTemplateInit if aTemplateInit.structure == ExcludedLibraryTemplateInit1A.structure => false
      case aTemplateInit if aTemplateInit.structure == ExcludedLibraryTemplateInit1B.structure => false
      case _ => true
    })

    when(extensionPredicate2.apply(any[Init])).thenAnswer((templateInit: Init) => templateInit match {
      case aTemplateInit if aTemplateInit.structure == ExcludedLibraryTemplateInit2.structure => false
      case _ => true
    })
  }

  forAll(TemplateInitExcludedScenarios) { (desc: String, templateInit: Init, expectedResult: Boolean) =>
    test(s"""The TemplateInit '$desc' should be ${if (expectedResult) "excluded" else "included"}""") {
      compositePredicate.apply(templateInit) shouldBe expectedResult
    }
  }

  private def initOf(typeName: String): Init = {
    Init(tpe = Type.Name(typeName), name = Name.Anonymous(), argss = List())
  }
}
