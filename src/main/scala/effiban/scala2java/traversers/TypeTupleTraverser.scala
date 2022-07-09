package effiban.scala2java.traversers

import effiban.scala2java.writers.JavaWriter

import scala.meta.Type

trait TypeTupleTraverser extends ScalaTreeTraverser[Type.Tuple]

private[traversers] class TypeTupleTraverserImpl(implicit javaWriter: JavaWriter) extends TypeTupleTraverser {

  import javaWriter._

  //tuple as type, e.g. x: (Int, String).
  override def traverse(tupleType: Type.Tuple): Unit = {
    //TODO if only 2 params, can be translated into Java Map.Entry, Apache Pair etc.
    //TODO if more than 2, can be converted to a JOOL tuple (should be an input option)
    writeComment(tupleType.toString())
  }
}
