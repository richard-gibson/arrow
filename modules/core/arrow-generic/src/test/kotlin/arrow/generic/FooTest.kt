package arrow.generic

import arrow.product
import arrow.typeclasses.*
import arrow.core.*
import arrow.instances.*

@product
data class Foo<T1, T2>(val i: Int, val bar: T1, val baz: T2) {
  companion object
}


fun arrow.generic.Foo.combine(other: arrow.generic.Foo): arrow.generic.Foo =
    this + other

fun List<arrow.generic.Foo>.combineAll(): arrow.generic.Foo =
    this.reduce { a, b -> a + b }

operator fun arrow.generic.Foo.plus(other: arrow.generic.Foo): arrow.generic.Foo =
    with(arrow.generic.Foo.semigroup()) { this@plus.combine(other) }


fun emptyFoo(): arrow.generic.Foo =
    arrow.generic.Foo.monoid().empty()



fun arrow.generic.Foo.tupled(): arrow.core.Tuple3<kotlin.Int, T1, T2> =
    arrow.core.Tuple3(this.i, this.bar, this.baz)

fun arrow.generic.Foo.tupledLabeled(): arrow.core.Tuple3<arrow.core.Tuple2<String, kotlin.Int>, arrow.core.Tuple2<String, T1>, arrow.core.Tuple2<String, T2>> =
    arrow.core.Tuple3(("i" toT i), ("bar" toT bar), ("baz" toT baz))

fun <B> arrow.generic.Foo.foldLabeled(f: (arrow.core.Tuple2<kotlin.String, kotlin.Int>, arrow.core.Tuple2<kotlin.String, T1>, arrow.core.Tuple2<kotlin.String, T2>) -> B): B {
  val t = tupledLabeled()
  return f(t.a, t.b, t.c)
}

fun arrow.core.Tuple3<kotlin.Int, T1, T2>.toFoo(): arrow.generic.Foo =
    arrow.generic.Foo(this.a, this.b, this.c)



fun arrow.generic.Foo.toHList(): arrow.generic.HList3<kotlin.Int, T1, T2> =
    arrow.generic.hListOf(this.i, this.bar, this.baz)

fun arrow.generic.HList3<kotlin.Int, T1, T2>.toFoo(): arrow.generic.Foo =
    arrow.generic.Foo(this.head, this.tail.head, this.tail.tail.head)

fun arrow.generic.Foo.toHListLabeled(): arrow.generic.HList3<arrow.core.Tuple2<String, kotlin.Int>, arrow.core.Tuple2<String, T1>, arrow.core.Tuple2<String, T2>> =
    arrow.generic.hListOf(("i" toT i), ("bar" toT bar), ("baz" toT baz))



fun <F> arrow.typeclasses.Applicative<F>.mapToFoo(i: arrow.Kind<F, kotlin.Int>, bar: arrow.Kind<F, T1>, baz: arrow.Kind<F, T2>): arrow.Kind<F, arrow.generic.Foo> =
    this.map(i, bar, baz, { it.toFoo() })




interface FooSemigroupInstance : arrow.typeclasses.Semigroup<arrow.generic.Foo> {
  override fun arrow.generic.Foo.combine(b: arrow.generic.Foo): arrow.generic.Foo {
    val (xA, xB, xC) = this
    val (yA, yB, yC) = b
    return arrow.generic.Foo(with(kotlin.Int.semigroup()){ xA.combine(yA) }, with(T1.semigroup()){ xB.combine(yB) }, with(T2.semigroup()){ xC.combine(yC) })
  }

  companion object {
    val defaultInstance : arrow.typeclasses.Semigroup<arrow.generic.Foo> =
        object : FooSemigroupInstance {}
  }
}

fun arrow.generic.Foo.Companion.semigroup(): arrow.typeclasses.Semigroup<arrow.generic.Foo> =
    FooSemigroupInstance.defaultInstance



interface FooMonoidInstance : arrow.typeclasses.Monoid<arrow.generic.Foo>, FooSemigroupInstance {
  override fun empty(): arrow.generic.Foo =
      arrow.generic.Foo(with(kotlin.Int.monoid()){ empty() }, with(T1.monoid()){ empty() }, with(T2.monoid()){ empty() })

  companion object {
    val defaultInstance : arrow.typeclasses.Monoid<arrow.generic.Foo> =
        object : FooMonoidInstance {}
  }
}

fun arrow.generic.Foo.Companion.monoid(): arrow.typeclasses.Monoid<arrow.generic.Foo> =
    FooMonoidInstance.defaultInstance



interface FooEqInstance : arrow.typeclasses.Eq<arrow.generic.Foo> {
  override fun arrow.generic.Foo.eqv(b: arrow.generic.Foo): Boolean =
      this == b

  companion object {
    val defaultInstance : arrow.typeclasses.Eq<arrow.generic.Foo> =
        object : FooEqInstance {}
  }
}

fun arrow.generic.Foo.Companion.eq(): arrow.typeclasses.Eq<arrow.generic.Foo> =
    FooEqInstance.defaultInstance


interface FooShowInstance : arrow.typeclasses.Show<arrow.generic.Foo> {
  override fun arrow.generic.Foo.show(): String =
      this.toString()
}

fun arrow.generic.Foo.Companion.show(): arrow.typeclasses.Show<arrow.generic.Foo> =
    object : FooShowInstance {}


