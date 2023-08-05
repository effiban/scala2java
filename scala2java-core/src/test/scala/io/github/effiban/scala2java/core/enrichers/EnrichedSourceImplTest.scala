package io.github.effiban.scala2java.core.enrichers

import io.github.effiban.scala2java.core.contexts.StatContext
import io.github.effiban.scala2java.core.enrichers.entities.matchers.EnrichedSourceScalatestMatcher.equalEnrichedSource
import io.github.effiban.scala2java.core.enrichers.entities.{EnrichedSimpleStat, EnrichedSource}
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import org.mockito.ArgumentMatchersSugar.{any, eqTo}

import scala.meta.{Stat, XtensionQuasiquoteSource, XtensionQuasiquoteTerm}

class EnrichedSourceImplTest extends UnitTestSuite {

  private val TheImport = q"import scala.abc"

  private val Pkg1 =
    q"""
    package pkg1 {
      case class Class1(x: Int)
    }
    """
  private val Pkg2 =
    q"""
    package pkg2 {
      case class Class2(y: Int)
    }
    """


  private val EnrichedPkg1 = EnrichedPkg(pkgRef = q"package1")
  private val EnrichedPkg2 = EnrichedPkg(pkgRef = q"package2")

  private val defaultStatEnricher = mock[DefaultStatEnricher]

  private val sourceEnricher = new SourceEnricherImpl(defaultStatEnricher)

  test("enrich()") {
    val TheSource =
      source"""
      import scala.abc

      package pkg1 {
        case class Class1(x: Int)
      }
      package pkg2 {
        case class Class2(y: Int)
      }
      """

    val expectedEnrichedSource = EnrichedSource(List(EnrichedSimpleStat(TheImport), EnrichedPkg1, EnrichedPkg2))

    doAnswer((stat: Stat, _: StatContext) => stat match {
      case aStat if aStat.structure == Pkg1.structure => EnrichedPkg1
      case aStat if aStat.structure == Pkg2.structure => EnrichedPkg2
      case aStat => EnrichedSimpleStat(aStat)
    }).when(defaultStatEnricher).enrich(any[Stat], eqTo(StatContext()))


    sourceEnricher.enrich(TheSource) should equalEnrichedSource(expectedEnrichedSource)
  }
}
