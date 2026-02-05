package net.mcbrawls.codex

import java.util.UUID

object UndashedUuid {
    fun fromString(string: String): UUID {
        require(string.indexOf(45.toChar()) == -1) { "Invalid undashed UUID string: $string" }
        return fromStringLenient(string)
    }

    fun fromStringLenient(string: String): UUID {
        return UUID.fromString(
            string.replaceFirst(
                "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})".toRegex(),
                "$1-$2-$3-$4-$5"
            )
        )
    }

    fun toString(uuid: UUID): String {
        return uuid.toString().replace("-", "")
    }
}
