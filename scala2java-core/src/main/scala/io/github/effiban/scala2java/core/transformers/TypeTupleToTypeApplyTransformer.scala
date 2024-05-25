package io.github.effiban.scala2java.core.transformers

import scala.meta.{Term, Type, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

trait TypeTupleToTypeApplyTransformer {

  def transform(typeTuple: Type.Tuple): Type.Apply
}

private[transformers] class TypeTupleToTypeApplyTransformerImpl(treeTransformer: => TreeTransformer)
  extends TypeTupleToTypeApplyTransformer {

  override def transform(typeTuple: Type.Tuple): Type.Apply = {
    val transformedArgs = typeTuple.args.map(treeTransformer.transform(_).asInstanceOf[Type])

    transformedArgs match {
      // 0 or 1 arg are both impossible - would fail parsing of the code before we get here
      // For a tuple of 2, using Java's Map.Entry type, for example: Map.Entry<String, Int>
      case arg1 :: arg2 :: Nil => Type.Apply(tpe = t"java.util.Map#Entry", args = List(arg1, arg2))
      // Java has no Tuple type for 3+ types, so we will use JOOL's Tuple types, for example: Tuple3<String, Int, Long>
      case args => Type.Apply(tpe = Type.Select(q"org.jooq.lambda.tuple", Type.Name(s"Tuple${args.length}")), args = args)
    }
  }
}
