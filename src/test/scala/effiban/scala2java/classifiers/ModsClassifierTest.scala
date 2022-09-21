package effiban.scala2java.classifiers

import effiban.scala2java.classifiers.ModsClassifier.{arePublic, includeFinal, includeSealed}
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

  private val IncludeSealedScenarios = Table[List[Mod], Boolean](
    ("Mods", "ExpectedResult"),
    (List(Mod.Sealed()), true),
    (List(Mod.Sealed(), PrivateAnonymous), true),
    (List(PrivateAnonymous), false),
    (List.empty[Mod], false),
  )

  private val IncludeFinalScenarios = Table[List[Mod], Boolean](
    ("Mods", "ExpectedResult"),
    (List(Mod.Final()), true),
    (List(Mod.Final(), PrivateAnonymous), true),
    (List(PrivateAnonymous), false),
    (List.empty[Mod], false),
  )

  forAll(ArePublicScenarios) { case (mods: List[Mod], expectedResult: Boolean) =>
    test(s"The mods $mods should be considered '${if (expectedResult) "public" else "non-public"}'") {
      arePublic(mods) shouldBe expectedResult
    }
  }

  forAll(IncludeSealedScenarios) { case (mods: List[Mod], expectedResult: Boolean) =>
    test(s"The mods $mods should be considered as '${if (expectedResult) "including sealed" else "not including sealed"}'") {
      includeSealed(mods) shouldBe expectedResult
    }
  }

  forAll(IncludeFinalScenarios) { case (mods: List[Mod], expectedResult: Boolean) =>
    test(s"The mods $mods should be considered as '${if (expectedResult) "including final" else "not including final"}'") {
      includeFinal(mods) shouldBe expectedResult
    }
  }
}
