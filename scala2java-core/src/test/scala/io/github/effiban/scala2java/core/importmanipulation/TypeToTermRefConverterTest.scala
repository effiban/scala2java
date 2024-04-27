package io.github.effiban.scala2java.core.importmanipulation

import io.github.effiban.scala2java.core.importmanipulation.TypeToTermRefConverter.toTermRefPath
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.{XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class TypeToTermRefConverterTest extends UnitTestSuite {

  test("generate for Type.Name 'T' should return Term.Name 'T'") {
    toTermRefPath(t"T").value.structure shouldBe q"T".structure
  }

  test("generate for Type.Select 'a.B' should return Term.Select 'a.B'") {
    toTermRefPath(t"a.B").value.structure shouldBe q"a.B".structure
  }

  test("generate for Type.Select 'a.b.C' should return Term.Select 'a.b.C'") {
    toTermRefPath(t"a.b.C").value.structure shouldBe q"a.b.C".structure
  }

  test("generate for 'A#B' should return Term.Select 'A.B'") {
    toTermRefPath(t"A#B").value.structure shouldBe q"A.B".structure
  }

  test("generate for 'a.B#C' should return Term.Select 'a.B.C'") {
    toTermRefPath(t"a.B#C").value.structure shouldBe q"a.B.C".structure
  }

  test("generate for 'A#B#C' should return Term.Select 'A.B.C'") {
    toTermRefPath(t"A#B#C").value.structure shouldBe q"A.B.C".structure
  }

  test("generate for 'a.B#C#D' should return 'a.B.C.D'") {
    toTermRefPath(t"a.B#C#D").value.structure shouldBe q"a.B.C.D".structure
  }

  test("generate for 'A[T]#B' should return None") {
    toTermRefPath(t"A[T]#B") shouldBe None
  }

  test("generate for Type.Apply 'A[T]' should return None") {
    toTermRefPath(t"A[T]") shouldBe None
  }
}
