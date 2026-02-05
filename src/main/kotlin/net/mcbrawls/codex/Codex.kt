package net.mcbrawls.codex

import com.mojang.serialization.Codec
import com.mojang.serialization.Decoder
import com.mojang.serialization.DynamicOps
import com.mojang.serialization.Encoder
import com.mojang.serialization.MapCodec

/**
 * Converts a codec of a type to a codec of the same type but functional.
 */
fun <A : Any?> MapCodec<A>.functionally(): MapCodec<() -> A> {
    return xmap({ { it } }, { it() })
}

/**
 * Encodes to a dynamic ops format.
 */
fun <A, T> Encoder<A>.encodeQuick(ops: DynamicOps<T>, input: A): T? {
    return encodeStart(ops, input)
        .result()
        .orElse(null)
}

/**
 * Decodes from a dynamic ops format.
 */
fun <A, T> Decoder<A>.decodeQuick(ops: DynamicOps<T>, input: T): A? {
    return parse(ops, input)
        .result()
        .orElse(null)
}

fun <F, S> nativePair(first: Codec<F>, second: Codec<S>): Codec<Pair<F, S>> = Codec.pair(first, second)
    .xmap(
        { it.first to it.second },
        { com.mojang.datafixers.util.Pair.of(it.first, it.second) }
    )
