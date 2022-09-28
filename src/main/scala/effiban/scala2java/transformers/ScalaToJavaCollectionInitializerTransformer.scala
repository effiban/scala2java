package effiban.scala2java.transformers

import effiban.scala2java.classifiers.TermNameClassifier

import scala.meta.Term.{Apply, ApplyType, Name, Select}

trait ScalaToJavaCollectionInitializerTransformer {
  def transform(termApply: Apply): Apply
}

private[transformers] class ScalaToJavaCollectionInitializerTransformerImpl(termNameClassifier: TermNameClassifier)
  extends ScalaToJavaCollectionInitializerTransformer {

  import termNameClassifier._

  // Transform collection initializers from Scala style into Java equivalents
  override def transform(termApply: Apply): Apply = {
    termApply match {
      case Apply(name: Name, elems) if isLazySeqLike(name) => Apply(Select(Name("Stream"), Name("of")), elems)
      case Apply(ApplyType(name: Name, types), elems) if isLazySeqLike(name) => Apply(ApplyType(Select(Name("Stream"), Name("of")), types), elems)

      case Apply(name: Name, elems) if isEagerSeqLike(name) => Apply(Select(Name("List"), Name("of")), elems)
      case Apply(ApplyType(name: Name, types), elems) if isEagerSeqLike(name) => Apply(ApplyType(Select(Name("List"), Name("of")), types), elems)

      case Apply(name: Name, elems) if isSetLike(name) => Apply(Select(Name("Set"), Name("of")), elems)
      case Apply(ApplyType(name: Name, types), elems) if isSetLike(name) => Apply(ApplyType(Select(Name("Set"), Name("of")), types), elems)

      case Apply(name: Name, elems) if isMapLike(name) => Apply(Select(Name("Map"), Name("ofEntries")), elems)
      case Apply(ApplyType(name: Name, types), elems) if isMapLike(name) => Apply(ApplyType(Select(Name("Map"), Name("ofEntries")), types), elems)

      case other => other
    }
  }
}

object ScalaToJavaCollectionInitializerTransformer extends ScalaToJavaCollectionInitializerTransformerImpl(TermNameClassifier)
