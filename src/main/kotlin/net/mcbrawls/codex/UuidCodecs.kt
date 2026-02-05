package net.mcbrawls.codex

import com.google.common.collect.Lists
import com.google.common.collect.Sets
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.Lifecycle
import java.util.Arrays
import java.util.UUID
import java.util.function.Supplier
import java.util.stream.IntStream

object UuidCodecs {
    val INT_STREAM_CODEC: Codec<UUID> = Codec.INT_STREAM
        .comapFlatMap(
            { uuidStream -> decodeFixedLengthArray(uuidStream, 4).map(::toUuid) },
            { uuid -> Arrays.stream(toIntArray(uuid)) }
        )

    val SET_CODEC: Codec<MutableSet<UUID>> = Codec.list(INT_STREAM_CODEC).xmap(Sets::newHashSet, Lists::newArrayList)

    val LINKED_SET_CODEC: Codec<MutableSet<UUID>> = Codec.list(INT_STREAM_CODEC).xmap(Sets::newLinkedHashSet, Lists::newArrayList)

    val STRING_CODEC: Codec<UUID> = Codec.STRING.comapFlatMap(
        { string ->
            return@comapFlatMap try {
                DataResult.success(UUID.fromString(string), Lifecycle.stable())
            } catch (var2: IllegalArgumentException) {
                DataResult.error { "Invalid UUID " + string + ": " + var2.message }
            }
        },
        UUID::toString
    )

    val CODEC: Codec<UUID> =
        Codec.withAlternative(
            Codec.STRING.comapFlatMap(
                { string ->
                    return@comapFlatMap try {
                        DataResult.success(UndashedUuid.fromStringLenient(string), Lifecycle.stable())
                    } catch (var2: IllegalArgumentException) {
                        DataResult.error { "Invalid UUID " + string + ": " + var2.message }
                    }
                },
                UndashedUuid::toString
            ),
            INT_STREAM_CODEC
        )

    fun decodeFixedLengthArray(stream: IntStream, length: Int): DataResult<IntArray> {
        val arr = stream.limit((length + 1).toLong()).toArray()
        if (arr.size != length) {
            val message = Supplier { "Input is not a list of $length ints" }
            return if (arr.size >= length) {
                DataResult.error(message, arr.copyOf(length))
            } else {
                DataResult.error(message)
            }
        } else {
            return DataResult.success(arr)
        }
    }

    fun toUuid(array: IntArray): UUID {
        return UUID(
            array[0].toLong() shl 32 or (array[1].toLong() and 4294967295L),
            array[2].toLong() shl 32 or (array[3].toLong() and 4294967295L)
        )
    }

    fun toIntArray(uuid: UUID): IntArray {
        val l = uuid.mostSignificantBits
        val m = uuid.leastSignificantBits
        return toIntArray(l, m)
    }

    private fun toIntArray(uuidMost: Long, uuidLeast: Long): IntArray {
        return intArrayOf((uuidMost shr 32).toInt(), uuidMost.toInt(), (uuidLeast shr 32).toInt(), uuidLeast.toInt())
    }
}
