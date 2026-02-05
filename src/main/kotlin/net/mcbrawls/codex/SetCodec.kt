package net.mcbrawls.codex

import com.mojang.datafixers.util.Pair
import com.mojang.datafixers.util.Unit
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps
import com.mojang.serialization.Lifecycle
import org.apache.commons.lang3.mutable.MutableObject
import java.util.Objects
import java.util.stream.Stream

class SetCodec<A>(private val elementCodec: Codec<A>) : Codec<Set<A>> {
    override fun <T : Any?> encode(input: Set<A>, ops: DynamicOps<T>, prefix: T): DataResult<T> {
        val builder = ops.listBuilder()

        for (a in input) {
            builder.add(elementCodec.encodeStart(ops, a))
        }

        return builder.build(prefix)
    }

    override fun <T : Any?> decode(ops: DynamicOps<T>, input: T): DataResult<Pair<Set<A>, T>> {
        return ops.getList(input).setLifecycle(Lifecycle.stable()).flatMap { stream ->
            val read = mutableSetOf<A>()
            val failed = Stream.builder<T>()
            val result = MutableObject<DataResult<Unit>>(DataResult.success(Unit.INSTANCE, Lifecycle.stable()))

            stream.accept { t ->
                val element = elementCodec.decode(ops, t)
                element.error().ifPresent { failed.add(t) }
                result.value = result.get().apply2stable({ r, v ->
                    read.add(v.first)
                    return@apply2stable r
                }, element)
            }

            val elements = read.toSet()
            val errors = ops.createList(failed.build())

            val pair = Pair.of(elements, errors)

            return@flatMap result.get().map { pair }.setPartial(pair)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }

        if (other == null || javaClass != other.javaClass) {
            return false
        }

        val setCodec = other as SetCodec<*>
        return elementCodec == setCodec.elementCodec
    }

    override fun hashCode(): Int {
        return Objects.hash(elementCodec)
    }

    override fun toString(): String {
        return "SetCodec[$elementCodec]"
    }

    companion object {
        fun <A> Codec<A>.setOf(): SetCodec<A> {
            return SetCodec(this)
        }
    }
}
