package effiban.scala2java.classifiers

import effiban.scala2java.testsuites.UnitTestSuite

import scala.meta.{Mod, Name}

class ModsClassifierTest extends UnitTestSuite {

  private val PrivateAnonymous = Mod.Private(Name.Anonymous())
  private val PrivateSpecific = Mod.Private(Name("abc"))

  private val ProtectedAnonymous = Mod.Protected(Name.Anonymous())
  private val ProtectedSpecific = Mod.Protected(Name("abc"))

  private val Scenarios = Table[List[Mod], Boolean](
    ("Mods", "ExpectedResult"),
    (List(PrivateAnonymous), false),
    (List(PrivateSpecific), false),
    (List(ProtectedAnonymous), false),
    (List(ProtectedSpecific), false),
    (List(PrivateSpecific, ProtectedAnonymous), false),
    (List(PrivateSpecific, ProtectedAnonymous, Mod.Case()), false),
    (List(Mod.Case()), true),
    (List(Mod.Abstract(), Mod.Implicit()), true),
    (List.empty[Mod], true),
  )

  forAll(Scenarios) { case (mods: List[Mod], expectedResult: Boolean) =>
    test(s"The mods $mods should be considered '${if (expectedResult) "public" else "non-public"}'") {
      ModsClassifier.arePublic(mods) shouldBe expectedResult
    }
  }
}
