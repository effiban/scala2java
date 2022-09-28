package effiban.scala2java.classifiers

import scala.meta.Term

trait TermNameClassifier {
  def isLazySeqLike(termName: Term.Name): Boolean

  def isEagerSeqLike(termName: Term.Name): Boolean

  def isSetLike(termName: Term.Name): Boolean

  def isMapLike(termName: Term.Name): Boolean

  def isCollection(termName: Term.Name): Boolean
}

object TermNameClassifier extends TermNameClassifier {

  final val LazySeqLike: Set[String] = Set(
    "LazyList",
    "Stream",
  )

  final val EagerSeqLike: Set[String] = Set(
    "Seq",
    "LinearSeq",
    "IndexedSeq",
    "ArraySeq",
    "List",
    "Vector"
  )

  final val SetLike: Set[String] = Set(
    "Set",
    "HashSet",
    "SortedSet",
    "TreeSet",
    "ListSet"
  )

  final val MapLike: Set[String] = Set(
    "Map",
    "HashMap",
    "VectorMap",
    "ListMap",
    "SortedMap",
    "TreeMap",
    "TreeSeqMap"
  )

  final val All: Set[String] = LazySeqLike ++ EagerSeqLike ++ SetLike ++ MapLike

  override def isLazySeqLike(termName: Term.Name): Boolean = LazySeqLike.contains(termName.value)

  override def isEagerSeqLike(termName: Term.Name): Boolean = EagerSeqLike.contains(termName.value)

  override def isSetLike(termName: Term.Name): Boolean = SetLike.contains(termName.value)

  override def isMapLike(termName: Term.Name): Boolean = MapLike.contains(termName.value)

  override def isCollection(termName: Term.Name): Boolean = All.contains(termName.value)
}
