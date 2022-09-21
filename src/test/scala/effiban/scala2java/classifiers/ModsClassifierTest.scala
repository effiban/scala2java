package effiban.scala2java.classifiers

import effiban.scala2java.classifiers.ModsClassifier.{arePublic, areSealed}
import effiban.scala2java.testsuites.UnitTestSuite

import scala.meta.{Mod, Name}

class ModsClassifierTest extends UnitTestSuite {

  private val PrivateAnonymous = Mod.Private(Name.Anonymous())
  private val PrivateSpecific = Mod.Private(Name("abc"))

  private val ProtectedAnonymous = Mod.Protected(Name.Anonymous())
  private val ProtectedSpecific = Mod.Protected(Name("abc"))

  private val ArePublicScenarios = Table[List[Mod], Boolean](
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

  private val AreSealedScenarios = Table[List[Mod], Boolean](
    ("Mods", "ExpectedResult"),
    (List(Mod.Sealed()), true),
    (List(Mod.Sealed(), PrivateAnonymous), true),
    (List(PrivateAnonymous), false),
    (List.empty[Mod], false),
  )

  forAll(ArePublicScenarios) { case (mods: List[Mod], expectedResult: Boolean) =>
    test(s"The mods $mods should be considered '${if (expectedResult) "public" else "non-public"}'") {
      arePublic(mods) shouldBe expectedResult
    }
  }

  forAll(AreSealedScenarios) { case (mods: List[Mod], expectedResult: Boolean) =>
    test(s"The mods $mods should be considered '${if (expectedResult) "sealed" else "not sealed"}'") {
      areSealed(mods) shouldBe expectedResult
    }
  }
}
