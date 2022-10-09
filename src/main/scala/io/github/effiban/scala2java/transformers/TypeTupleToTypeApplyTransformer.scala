package io.github.effiban.scala2java.transformers

import scala.meta.Type

trait TypeTupleToTypeApplyTransformer {

  def transform(typeTuple: Type.Tuple): Type.Apply
}

object TypeTupleToTypeApplyTransformer extends TypeTupleToTypeApplyTransformer {

  override def transform(typeTuple: Type.Tuple): Type.Apply = {
    typeTuple.args match {
      // 0 or 1 arg are both impossible - would fail parsing of the code before we get here
      // For a tuple of 2, using Java's Map.Entry type, for example: Map.Entry<String, Int>
      case arg1 :: arg2 :: Nil => Type.Apply(tpe = Type.Project(Type.Name("Map"), Type.Name("Entry")), args = List(arg1, arg2))
      // Java has no Tuple type for 3+ types, so we will use JOOL's Tuple types, for example: Tuple3<String, Int, Long>
      case args => Type.Apply(tpe = Type.Name(s"Tuple${args.length}"), args = args)
    }
  }
}
