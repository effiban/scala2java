package effiban.scala2java.transformers

import scala.meta.Type

trait TypeByNameToSupplierTypeTransformer {
  def transform(typeByName: Type.ByName): Type.Apply
}

object TypeByNameToSupplierTypeTransformer extends TypeByNameToSupplierTypeTransformer {

  override def transform(typeByName: Type.ByName): Type.Apply = {
    // The closest analog in Java to a Scala parameter passed by name - is a Supplier type
    Type.Apply(Type.Name("Supplier"), List(typeByName.tpe))
  }
}
