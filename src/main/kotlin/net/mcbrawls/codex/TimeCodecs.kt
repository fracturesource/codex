package net.mcbrawls.codex

import com.mojang.serialization.Codec
import kotlin.time.Instant

object TimeCodecs {
    val INSTANT: Codec<Instant> = Codec.STRING.xmap(
        Instant::parse,
        Instant::toString,
    )
}
