package io.github.effiban.scala2java.core.enrichers

import io.github.effiban.scala2java.core.contexts._
import io.github.effiban.scala2java.core.enrichers.contexts.CtorSecondaryEnrichmentContext
import io.github.effiban.scala2java.core.enrichers.entities.EnrichedCtorSecondary
import io.github.effiban.scala2java.core.enrichers.entities.matchers.EnrichedCtorSecondaryScalatestMatcher.equalEnrichedCtorSecondary
import io.github.effiban.scala2java.core.entities.{JavaModifier, JavaTreeType}
import io.github.effiban.scala2java.core.matchers.ModifiersContextMatcher.eqModifiersContext
import io.github.effiban.scala2java.core.resolvers.JavaModifiersResolver
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.spi.entities.JavaScope.JavaScope

import scala.meta.{Ctor, Init, Mod, Name, Type, XtensionQuasiquoteInit, XtensionQuasiquoteTerm, XtensionQuasiquoteTermParam, XtensionQuasiquoteType}

class CtorSecondaryEnricherImplTest extends UnitTestSuite {

  private val ClassName = t"MyClass"

  private val TheCtorContext = CtorSecondaryEnrichmentContext(javaScope = JavaScope.Class, className = ClassName)

  private val TheAnnot = Mod.Annot(
    Init(tpe = Type.Name("MyAnnotation"), name = Name.Anonymous(), argss = List())
  )
  private val TheScalaMods = List(TheAnnot)

  private val TheJavaModifiers = List(JavaModifier.Public)

  private val CtorArg1 = param"param1: Int"
  private val CtorArg2 = param"param2: Int"
  private val CtorArg3 = param"param3: Int"
  private val CtorArg4 = param"param4: Int"

  private val CtorArgList1 = List(CtorArg1, CtorArg2)
  private val CtorArgList2 = List(CtorArg3, CtorArg4)

  private val TheSelfInit = init"this(param1)"

  private val Statement1 = q"doSomething1(param1)"
  private val Statement2 = q"doSomething2(param2)"

  private val javaModifiersResolver = mock[JavaModifiersResolver]

  private val ctorSecondaryEnricher = new CtorSecondaryEnricherImpl(javaModifiersResolver)

  test("enrich()") {
    val javaScope = JavaScope.Class

    val ctorSecondary = Ctor.Secondary(
      mods = TheScalaMods,
      name = Name.Anonymous(),
      paramss = List(CtorArgList1, CtorArgList2),
      init = TheSelfInit,
      stats = List(Statement1, Statement2)
    )

    val expectedEnrichedCtorSecondary = EnrichedCtorSecondary(
      stat = ctorSecondary,
      className = ClassName,
      javaModifiers = TheJavaModifiers
    )

    doReturn(TheJavaModifiers).when(javaModifiersResolver).resolve(eqExpectedScalaMods(ctorSecondary, javaScope))

    val actualEnrichedCtorSecondary = ctorSecondaryEnricher.enrich(ctorSecondary, TheCtorContext)
    actualEnrichedCtorSecondary should equalEnrichedCtorSecondary(expectedEnrichedCtorSecondary)
  }

  private def eqExpectedScalaMods(ctorSecondary: Ctor.Secondary, javaScope: JavaScope) = {
    val expectedModifiersContext = ModifiersContext(ctorSecondary, JavaTreeType.Method, javaScope)
    eqModifiersContext(expectedModifiersContext)
  }
}
