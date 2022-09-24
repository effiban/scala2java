package effiban.scala2java.typeinference

import effiban.scala2java.testsuites.UnitTestSuite

import scala.meta.{Term, Type}

class NameTypeInferrerTest extends UnitTestSuite {

  private final val OptionTypeName = Type.Name("Option")
  private final val EitherTypeName = Type.Name("Either")

  private val TermNameToTypeNameMappings = Table(
    ("TermName", "ExpectedMaybeTypeName"),
    (Term.Name("Option"), Some(OptionTypeName)),
    (Term.Name("Some"), Some(OptionTypeName)),
    (Term.Name("None"), Some(OptionTypeName)),
    (Term.Name("Right"), Some(EitherTypeName)),
    (Term.Name("Left"), Some(EitherTypeName)),
    (Term.Name("Future"), Some(Type.Name("Future"))),
    (Term.Name("Stream"), Some(Type.Name("Stream"))),
    (Term.Name("Array"), Some(Type.Name("Array"))),
    (Term.Name("List"), Some(Type.Name("List"))),
    (Term.Name("Vector"), Some(Type.Name("Vector"))),
    (Term.Name("Seq"), Some(Type.Name("Seq"))),
    (Term.Name("Set"), Some(Type.Name("Set"))),
    (Term.Name("Map"), Some(Type.Name("Map"))),
    (Term.Name("foo"), None)
  )

  forAll(TermNameToTypeNameMappings) {
    (termName: Term.Name, expectedMaybeTypeName: Option[Type.Name]) => {
      test(s"Infer $termName should return $expectedMaybeTypeName") {
        expectedMaybeTypeName match {
          case Some(expectedTypeName) => NameTypeInferrer.infer(termName).value.structure shouldBe expectedTypeName.structure
          case None => NameTypeInferrer.infer(termName) shouldBe None
        }
      }
    }
  }

}
