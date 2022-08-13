package effiban.scala2java.typeinference

import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.testtrees.TypeNames
import org.mockito.ArgumentMatchers.any

import scala.meta.Term.{If, New, Throw}
import scala.meta.{Init, Lit, Name, Term, Type}

class IfTypeInferrerImplTest extends UnitTestSuite {

  private val Condition = Term.ApplyInfix(
    lhs = Term.Name("x"),
    op = Term.Name("<"),
    targs = Nil,
    args = List(Lit.Int(3))
  )

  private val MyExceptionInit = Init(tpe = Type.Name("MyException"), name = Name.Anonymous(), argss = Nil)

  private val termTypeInferrer = mock[TermTypeInferrer]

  private val ifTypeInferrer = new IfTypeInferrerImpl(termTypeInferrer)

  test("infer() for 'then' only should return AnonymousName") {
    val thenp = Lit.String("abc")

    val `if` = If(cond = Condition, thenp = thenp, elsep = Lit.Unit())

    when(termTypeInferrer.infer(any[Term])).thenAnswer((term: Term) =>
      term match {
        case aTerm if aTerm.structure == thenp.structure => Some(TypeNames.String)
        case aTerm if aTerm.structure == Lit.Unit().structure => Some(TypeNames.Unit)
      }
    )

    ifTypeInferrer.infer(`if`).value.structure shouldBe Type.AnonymousName().structure
  }

  test("infer() for 'then' and 'else' which have the same type should return it") {
    val thenp = Lit.String("abc")
    val elsep = Lit.String("def")

    val `if` = If(cond = Condition, thenp = thenp, elsep = elsep)

    when(termTypeInferrer.infer(any[Term])).thenReturn(Some(TypeNames.String))

    ifTypeInferrer.infer(`if`).value.structure shouldBe TypeNames.String.structure

    verify(termTypeInferrer).infer(eqTree(thenp))
    verify(termTypeInferrer).infer(eqTree(elsep))
  }

  test("infer() for 'then' and 'else' which have different types should return None") {
    val thenp = Lit.String("abc")
    val elsep = Lit.Int(3)

    val `if` = If(cond = Condition, thenp = thenp, elsep = elsep)

    when(termTypeInferrer.infer(any[Term])).thenAnswer((term: Term) =>
      term match {
        case aTerm if aTerm.structure == thenp.structure => Some(TypeNames.String)
        case aTerm if aTerm.structure == elsep.structure => Some(TypeNames.Int)
      }
    )

    ifTypeInferrer.infer(`if`) shouldBe None
  }

  test("infer() for 'then' and 'else' when 'then' has anonymous type should return type of 'else'") {
    val thenp = Throw(New(MyExceptionInit))
    val elsep = Lit.String("abc")

    when(termTypeInferrer.infer(any[Term])).thenAnswer((term: Term) =>
      term match {
        case aTerm if aTerm.structure == thenp.structure => Some(Type.AnonymousName())
        case aTerm if aTerm.structure == elsep.structure => Some(TypeNames.Int)
      }
    )

    val `if` = If(cond = Condition, thenp = thenp, elsep = elsep)

    ifTypeInferrer.infer(`if`).value.structure shouldBe TypeNames.Int.structure
  }

  test("infer() for 'then' and 'else' when 'else' has anonymous type should return type of 'then'") {
    val thenp = Lit.String("abc")
    val elsep = Throw(New(MyExceptionInit))

    when(termTypeInferrer.infer(any[Term])).thenAnswer((term: Term) =>
      term match {
        case aTerm if aTerm.structure == thenp.structure => Some(TypeNames.Int)
        case aTerm if aTerm.structure == elsep.structure => Some(Type.AnonymousName())
      }
    )

    val `if` = If(cond = Condition, thenp = thenp, elsep = elsep)

    ifTypeInferrer.infer(`if`).value.structure shouldBe TypeNames.Int.structure
  }

  test("infer() for 'then' and 'else' when 'then' has unknown type should return None") {
    val thenp = Term.Name("blabla")
    val elsep = Lit.String("abc")

    when(termTypeInferrer.infer(any[Term])).thenAnswer((term: Term) =>
      term match {
        case aTerm if aTerm.structure == thenp.structure => None
        case aTerm if aTerm.structure == elsep.structure => Some(TypeNames.Int)
      }
    )

    val `if` = If(cond = Condition, thenp = thenp, elsep = elsep)

    ifTypeInferrer.infer(`if`) shouldBe None
  }

  test("infer() for 'then' and 'else' when 'else' has unknown type should return None") {
    val thenp = Lit.String("abc")
    val elsep = Term.Name("blabla")

    when(termTypeInferrer.infer(any[Term])).thenAnswer((term: Term) =>
      term match {
        case aTerm if aTerm.structure == thenp.structure => Some(TypeNames.String)
        case aTerm if aTerm.structure == elsep.structure => None
      }
    )

    val `if` = If(cond = Condition, thenp = thenp, elsep = elsep)

    ifTypeInferrer.infer(`if`) shouldBe None
  }

  test("infer() for 'then' and 'else' when both have unknown types should return None") {
    val thenp = Term.Name("blabla1")
    val elsep = Term.Name("blabla2")

    when(termTypeInferrer.infer(any[Term])).thenAnswer((term: Term) =>
      term match {
        case aTerm if aTerm.structure == thenp.structure => None
        case aTerm if aTerm.structure == elsep.structure => None
      }
    )

    val `if` = If(cond = Condition, thenp = thenp, elsep = elsep)

    ifTypeInferrer.infer(`if`) shouldBe None
  }
}
