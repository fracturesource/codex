package net.mcbrawls.codex

import com.mojang.serialization.Codec
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Instant

object TimeCodecs {
    val INSTANT: Codec<Instant> = Codec.STRING.xmap(
        Instant::parse,
        Instant::toString,
    )

    val DURATION: Codec<Duration> = Codec.LONG.xmap(
        { it.milliseconds },
        { it.inWholeMilliseconds },
    )
}
