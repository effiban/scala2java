package effiban.scala2java.entities

object ScalaCollectionNames {

  final val SeqLike: Set[String] = Set(
    "Seq",
    "LinearSeq",
    "IndexedSeq",
    "ArraySeq",
    "List",
    "LazyList",
    "Stream",
    "Queue",
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

  final val All: Set[String] = SeqLike ++ SetLike ++ MapLike

  def isSeqLike(name: String): Boolean = SeqLike.contains(name)

  def isSetLike(name: String): Boolean = SetLike.contains(name)

  def isMapLike(name: String): Boolean = MapLike.contains(name)

  def isCollection(name: String): Boolean = All.contains(name)
}
