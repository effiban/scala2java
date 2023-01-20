package io.github.effiban.scala2java.core.resolvers

import io.github.effiban.scala2java.core.classifiers.DefnTypeClassifier
import io.github.effiban.scala2java.core.contexts.TemplateBodyContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.spi.entities.JavaScope.JavaScope
import org.mockito.ArgumentMatchers.any

import scala.meta.{Defn, XtensionQuasiquoteInit, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class TemplateChildrenResolverImplTest extends UnitTestSuite {

  private val TheClassName = t"MyClass"

  private val ThePrimaryCtor = q"def this(param1: Int, param2: String)"

  private val TheInits = List(
    init"Parent1()",
    init"Parent2()"
  )

  private val TermApply1 = q"doSomething1(arg1, arg2)"
  private val TermApply2 = q"doSomething2(arg3, arg4)"

  private val TheTerms = List(TermApply1, TermApply2)

  private val DefnDef1 = q"def doSomething1(param1: Int, param2: String): Unit = {}"
  private val DefnDef2 = q"def doSomething2(param3: Int, param4: String): Unit = {}"

  private val EnumTypeDef = q"type MyValue = Value"
  private val RegularTypeDef = q"type MyType = MyOtherType"

  private val defnTypeClassifier = mock[DefnTypeClassifier]

  private val templateChildrenResolver = new TemplateChildrenResolverImpl(defnTypeClassifier)

  override def beforeEach(): Unit = {
    super.beforeEach()

    when(defnTypeClassifier.isEnumTypeDef(any[Defn.Type], any[JavaScope]))
      .thenAnswer((defnType: Defn.Type, javaScope: JavaScope) =>
        (defnType, javaScope) match {
          case (aDefnType, JavaScope.Enum) if aDefnType.structure == EnumTypeDef.structure => true
          case _ => false
        }
      )
  }

  test("resolve() when has primary ctor. and no type defs") {
    val context = TemplateBodyContext(
      JavaScope.Class,
      Some(TheClassName),
      Some(ThePrimaryCtor),
      TheInits
    )

    val nonTerms = List(DefnDef1, DefnDef2)

    val expectedChildren = nonTerms :+ ThePrimaryCtor

    templateChildrenResolver.resolve(TheTerms, nonTerms, context).structure shouldBe expectedChildren.structure
  }

  test("resolve() when has a primary ctor. and has a regular type def") {
    val context = TemplateBodyContext(
      JavaScope.Class,
      Some(TheClassName),
      Some(ThePrimaryCtor),
      TheInits
    )

    val nonTerms = List(DefnDef1, DefnDef2, RegularTypeDef)

    val expectedChildren = nonTerms :+ ThePrimaryCtor

    templateChildrenResolver.resolve(TheTerms, nonTerms, context).structure shouldBe expectedChildren.structure
  }

  test("resolve() when has a primary ctor., in Java enum scope and has enum type def") {
    val context = TemplateBodyContext(
      JavaScope.Enum,
      Some(TheClassName),
      Some(ThePrimaryCtor),
      TheInits
    )

    val nonTerms = List(DefnDef1, DefnDef2, EnumTypeDef)

    val expectedChildren = List(DefnDef1, DefnDef2) :+ ThePrimaryCtor

    templateChildrenResolver.resolve(TheTerms, nonTerms, context).structure shouldBe expectedChildren.structure
  }

  test("resolve() when has no primary ctor. and no type defs") {
    val context = TemplateBodyContext(
      JavaScope.Class,
      Some(TheClassName),
      None,
      TheInits
    )

    val nonTerms = List(DefnDef1, DefnDef2)

    val expectedChildren = TheTerms ++ nonTerms

    templateChildrenResolver.resolve(TheTerms, nonTerms, context).structure shouldBe expectedChildren.structure
  }

  test("resolve() when has no primary ctor. and has a regular type def") {
    val context = TemplateBodyContext(
      JavaScope.Class,
      Some(TheClassName),
      None,
      TheInits
    )

    val nonTerms = List(DefnDef1, DefnDef2, RegularTypeDef)

    val expectedChildren = TheTerms ++ nonTerms

    templateChildrenResolver.resolve(TheTerms, nonTerms, context).structure shouldBe expectedChildren.structure
  }

  test("resolve() when has no primary ctor., in Java enum scope and has enum type def") {
    val context = TemplateBodyContext(
      JavaScope.Enum,
      Some(TheClassName),
      None,
      TheInits
    )

    val nonTerms = List(DefnDef1, DefnDef2, EnumTypeDef)

    val expectedChildren = TheTerms ++ List(DefnDef1, DefnDef2)

    templateChildrenResolver.resolve(TheTerms, nonTerms, context).structure shouldBe expectedChildren.structure
  }
}
