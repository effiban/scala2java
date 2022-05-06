package com.effiban.scala2java

import org.mockito.ArgumentMatchers.any

import scala.meta.{Term, Type}

class ApplyTypeTraverserImplTest extends UnitTestSuite {

  private val typeTraverser = mock[TypeTraverser]
  private val termTraverser = mock[TermTraverser]
  private val typeListTraverser = mock[TypeListTraverser]

  private val applyTypeTraverser = new ApplyTypeTraverserImpl(typeTraverser, termTraverser, typeListTraverser)

  override def beforeEach(): Unit = {
    super.beforeEach()
    doAnswer((`type`: Type) => outputWriter.write(`type`.toString())).when(typeTraverser).traverse(any[Type])
    doAnswer((`term`: Term) => outputWriter.write(term.toString())).when(termTraverser).traverse(any[Term])
    doAnswer((typeList: List[Type]) => outputWriter.write(typeListToString(typeList))).when(typeListTraverser).traverse(any[List[Type]])
  }

  test("traverse() when function is 'classOf' should convert to the Java equivalent") {
    applyTypeTraverser.traverse(Term.ApplyType(fun = Term.Name("classOf"), targs = List(Type.Name("T"))))

    outputWriter.toString shouldBe "T.class"
  }

  test("traverse() when function is regular should handle accordingly") {
    applyTypeTraverser.traverse(Term.ApplyType(fun = Term.Name("myFunc"), targs = List(Type.Name("T1"), Type.Name("T2"))))

    outputWriter.toString shouldBe "myFunc.<T1,T2>"
  }

  private def typeListToString(typeList: List[Type]) = s"<${typeList.mkString(",")}>"
}
