package effiban.scala2java.transformers

import effiban.scala2java.entities.ScalaCollectionNames.{isMapLike, isSeqLike, isSetLike}

import scala.meta.Term.{Apply, ApplyType, Name, Select}

trait ScalaToJavaCollectionInitializerTransformer {
  def transform(termApply: Apply): Apply
}

object ScalaToJavaCollectionInitializerTransformer extends ScalaToJavaCollectionInitializerTransformer {

  // Transform collection initializers from Scala style into Java equivalents
  override def transform(termApply: Apply): Apply = {
    termApply match {
      case Apply(Name(name), elems) if isSeqLike(name) => Apply(Select(Name("List"), Name("of")), elems)
      case Apply(ApplyType(Name(name), types), elems) if isSeqLike(name) => Apply(Select(ApplyType(Name("List"), types), Name("of")), elems)

      case Apply(Name(name), elems) if isSetLike(name) => Apply(Select(Name("Set"), Name("of")), elems)
      case Apply(ApplyType(Name(name), types), elems) if isSetLike(name) => Apply(Select(ApplyType(Name("Set"), types), Name("of")), elems)

      case Apply(Name(name), elems) if isMapLike(name) => Apply(Select(Name(name), Name("ofEntries")), elems)
      case Apply(ApplyType(Name(name), types), elems) if isMapLike(name) => Apply(Select(ApplyType(Name("Map"), types), Name("ofEntries")), elems)

      case other => other
    }
  }
}
