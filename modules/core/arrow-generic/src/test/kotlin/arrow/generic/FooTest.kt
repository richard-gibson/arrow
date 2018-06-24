package arrow.generic

import arrow.product
import arrow.typeclasses.*
import arrow.core.*
import arrow.instances.*
import arrow.typeclasses.Monoid
import arrow.typeclasses.Semigroup

@product
data class Foo<T1, T2>(val i: Int, val bar: T1, val baz: T2) {
  companion object
}


fun <T1, T2> arrow.generic.Foo<T1, T2>.combine(other: arrow.generic.Foo<T1, T2>): arrow.generic.Foo<T1, T2> =
    this + other

fun <T1, T2> List<arrow.generic.Foo<T1, T2>>.combineAll(): arrow.generic.Foo<T1, T2> =
    this.reduce { a, b -> a + b }

/**
 * can't override with semigroups as params, need to bring
 *  SGT1: arrow.typeclasses.Semigroup<T1>, SGT2: arrow.typeclasses.Semigroup<T2>
 * into scope some other way
 **/
operator fun <T1, T2> arrow.generic.Foo<T1, T2>.plus(other: arrow.generic.Foo<T1, T2>): arrow.generic.Foo<T1, T2> =
    with(arrow.generic.Foo.semigroup(SGT1, SGT2)) { this@plus.combine(other) }


fun <T1, T2> emptyFoo(MAT1: arrow.typeclasses.Monoid<T1>, MAT2: arrow.typeclasses.Monoid<T2>): arrow.generic.Foo<T1, T2> =
    arrow.generic.Foo.monoid(MAT1, MAT2).empty()



fun <T1, T2> arrow.generic.Foo<T1, T2>.tupled(): arrow.core.Tuple3<kotlin.Int, T1, T2> =
    arrow.core.Tuple3(this.i, this.bar, this.baz)

fun <T1, T2> arrow.generic.Foo<T1, T2>.tupledLabeled(): arrow.core.Tuple3<arrow.core.Tuple2<String, kotlin.Int>, arrow.core.Tuple2<String, T1>, arrow.core.Tuple2<String, T2>> =
    arrow.core.Tuple3(("i" toT i), ("bar" toT bar), ("baz" toT baz))

fun <T1, T2, B> arrow.generic.Foo<T1, T2>.foldLabeled(f: (arrow.core.Tuple2<kotlin.String, kotlin.Int>, arrow.core.Tuple2<kotlin.String, T1>, arrow.core.Tuple2<kotlin.String, T2>) -> B): B {
  val t = tupledLabeled()
  return f(t.a, t.b, t.c)
}

fun <T1, T2> arrow.core.Tuple3<kotlin.Int, T1, T2>.toFoo(): arrow.generic.Foo<T1, T2> =
    arrow.generic.Foo(this.a, this.b, this.c)



fun <T1, T2> arrow.generic.Foo<T1, T2>.toHList(): arrow.generic.HList3<kotlin.Int, T1, T2> =
    arrow.generic.hListOf(this.i, this.bar, this.baz)

fun <T1, T2> arrow.generic.HList3<kotlin.Int, T1, T2>.toFoo(): arrow.generic.Foo<T1, T2> =
    arrow.generic.Foo(this.head, this.tail.head, this.tail.tail.head)

fun <T1, T2> arrow.generic.Foo<T1, T2>.toHListLabeled(): arrow.generic.HList3<arrow.core.Tuple2<String, kotlin.Int>, arrow.core.Tuple2<String, T1>, arrow.core.Tuple2<String, T2>> =
    arrow.generic.hListOf(("i" toT i), ("bar" toT bar), ("baz" toT baz))



fun <F, T1, T2> arrow.typeclasses.Applicative<F>.mapToFoo(i: arrow.Kind<F, kotlin.Int>, bar: arrow.Kind<F, T1>, baz: arrow.Kind<F, T2>): arrow.Kind<F, arrow.generic.Foo<T1, T2>> =
    this.map(i, bar, baz, { it.toFoo() })




interface FooSemigroupInstance<T1, T2> : arrow.typeclasses.Semigroup<arrow.generic.Foo<T1, T2>> {
  val SGT1: arrow.typeclasses.Semigroup<T1>
  val SGT2: arrow.typeclasses.Semigroup<T2>

  override fun arrow.generic.Foo<T1, T2>.combine(b: arrow.generic.Foo<T1, T2>): arrow.generic.Foo<T1, T2> {
    val (xA, xB, xC) = this
    val (yA, yB, yC) = b
    return arrow.generic.Foo(with(kotlin.Int.semigroup()){ xA.combine(yA) }, with(SGT1){ xB.combine(yB) }, with(SGT2){ xC.combine(yC) })
  }

  companion object {
    fun <T1, T2> defaultInstance(SGT1: arrow.typeclasses.Semigroup<T1>, SGT2: arrow.typeclasses.Semigroup<T2>) : arrow.typeclasses.Semigroup<arrow.generic.Foo<T1, T2>> =
        object : FooSemigroupInstance<T1, T2> {
          override val SGT1: Semigroup<T1> = SGT1
          override val SGT2: Semigroup<T2> = SGT2
        }
  }
}

fun <T1, T2> arrow.generic.Foo.Companion.semigroup(SGT1: arrow.typeclasses.Semigroup<T1>, SGT2: arrow.typeclasses.Semigroup<T2>): arrow.typeclasses.Semigroup<arrow.generic.Foo<T1, T2>> =
    FooSemigroupInstance.defaultInstance(SGT1, SGT2)



interface FooMonoidInstance<T1, T2> : arrow.typeclasses.Monoid<arrow.generic.Foo<T1, T2>>, FooSemigroupInstance<T1, T2> {
  val MAT1: arrow.typeclasses.Monoid<T1>
  val MAT2: arrow.typeclasses.Monoid<T2>
  override fun empty(): arrow.generic.Foo<T1, T2> =
      arrow.generic.Foo(with(kotlin.Int.monoid()){ empty() }, with(MAT1){ empty() }, with(MAT2){ empty() })

  companion object {
    fun <T1, T2> defaultInstance(MAT1: arrow.typeclasses.Monoid<T1>, MAT2: arrow.typeclasses.Monoid<T2>)
        : arrow.typeclasses.Monoid<arrow.generic.Foo<T1, T2>> =
        object : FooMonoidInstance<T1, T2> {
          override val SGT1: Semigroup<T1> = MAT1
          override val SGT2: Semigroup<T2> = MAT2
          override val MAT1: Monoid<T1> = MAT1
          override val MAT2: Monoid<T2> = MAT2
        }
  }
}

fun <T1, T2>arrow.generic.Foo.Companion.monoid(MAT1: arrow.typeclasses.Monoid<T1>, MAT2: arrow.typeclasses.Monoid<T2>):
    arrow.typeclasses.Monoid<arrow.generic.Foo<T1, T2>> =
    FooMonoidInstance.defaultInstance(MAT1, MAT2)



interface FooEqInstance<T1, T2> : arrow.typeclasses.Eq<arrow.generic.Foo<T1, T2>> {
  override fun arrow.generic.Foo<T1, T2>.eqv(b: arrow.generic.Foo<T1, T2>): Boolean =
      this == b

  companion object {
    fun <T1, T2> defaultInstance() : arrow.typeclasses.Eq<arrow.generic.Foo<T1, T2>> =
        object : FooEqInstance<T1, T2> {}
  }
}

fun <T1, T2> arrow.generic.Foo.Companion.eq(): arrow.typeclasses.Eq<arrow.generic.Foo<T1, T2>> =
    FooEqInstance.defaultInstance<T1, T2>()


interface FooShowInstance<T1, T2> : arrow.typeclasses.Show<arrow.generic.Foo<T1, T2>> {
  override fun arrow.generic.Foo<T1, T2>.show(): String =
      this.toString()
}

fun <T1, T2> arrow.generic.Foo.Companion.show(): arrow.typeclasses.Show<arrow.generic.Foo<T1, T2>> =
    object : FooShowInstance<T1, T2> {}


