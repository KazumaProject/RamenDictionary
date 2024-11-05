import java.io.InputStream
import java.nio.ByteBuffer
import java.util.*

fun BitSet.rank0(index: Int): Int {
    var count = 0
    for (i in 0..index) {
        if (!this[i]) {
            count++
        }
    }
    return count
}

fun BitSet.rank1(index: Int): Int {
    return index + 1 - rank0(index)
}

fun BitSet.select0(nodeId: Int): Int {
    var count = 0
    for (i in 0 until size()) {
        if (!this[i]) {
            count++
            if (count == nodeId) {
                return i
            }
        }
    }
    return -1 // Not found
}

fun BitSet.select1(nodeId: Int): Int {
    var count = 0
    for (i in 0 until size()) {
        if (this[i]) {
            count++
            if (count == nodeId) {
                return i
            }
        }
    }
    return -1 // Not found
}

fun BitSet.rank1GetIntArray(): IntArray {
    val n = this.size()
    val rank = IntArray(n + 1)
    for (i in 1..n) {
        rank[i] = rank[i - 1] + if (this[i - 1]) 1 else 0
    }
    return rank
}

fun BitSet.rank0GetIntArray(): IntArray {
    val n = this.size()
    val rank = IntArray(n + 1)
    for (i in 1..n) {
        rank[i] = rank[i - 1] + if (this[i - 1]) 0 else 1
    }
    return rank
}

fun BitSet.rank1GetShortArray(): ShortArray {
    val n = this.size()
    val rank = ShortArray(n + 1)
    for (i in 1..n) {
        rank[i] = (rank[i - 1] + if (this[i - 1]) 1 else 0).toShort()
    }
    return rank
}

fun BitSet.rank0GetShortArray(): ShortArray {
    val n = this.size()
    val rank = ShortArray(n + 1)
    for (i in 1..n) {
        rank[i] = (rank[i - 1] + if (this[i - 1]) 0 else 1).toShort()
    }
    return rank
}

fun BitSet.rank1Common(i: Int, rank1: IntArray): Int {
    return rank1[i + 1]
}

fun BitSet.rank0Common(i: Int, rank0: IntArray): Int {
    return rank0[i + 1]
}

fun BitSet.select1Common(j: Int, rank1: IntArray): Int {
    var low = 0
    var high = this.size() - 1
    while (low <= high) {
        val mid = (low + high) / 2
        if (rank1[mid + 1] > j) {
            high = mid - 1
        } else if (rank1[mid + 1] < j) {
            low = mid + 1
        } else {
            if (this[mid]) return mid
            high = mid - 1
        }
    }
    return -1
}

// Extension function for select0 in O(log n) time complexity
fun BitSet.select0Common(j: Int, rank0: IntArray): Int {
    var low = 0
    var high = this.size() - 1
    while (low <= high) {
        val mid = (low + high) / 2
        if (rank0[mid + 1] > j) {
            high = mid - 1
        } else if (rank0[mid + 1] < j) {
            low = mid + 1
        } else {
            if (!this[mid]) return mid
            high = mid - 1
        }
    }
    return -1
}

// Extension function to get the number of 1s up to and including index i
fun BitSet.rank1CommonShort(i: Int, rank1: ShortArray): Short {
    return rank1[i + 1]
}

// Extension function to get the number of 0s up to and including index i
fun BitSet.rank0CommonShort(i: Short, rank0: ShortArray): Short {
    return rank0[i + 1]
}

// Extension function for select1 in O(log n) time complexity
fun BitSet.select1CommonShort(j: Short, rank1: ShortArray): Short {
    var low = 0
    var high = this.size() - 1
    while (low <= high) {
        val mid = (low + high) / 2
        if (rank1[mid + 1] > j) {
            high = mid - 1
        } else if (rank1[mid + 1] < j) {
            low = mid + 1
        } else {
            if (this[mid]) return mid.toShort()
            high = mid - 1
        }
    }
    return -1
}

fun BitSet.select0CommonShort(j: Short, rank0: ShortArray): Short {
    var low = 0
    var high = this.size() - 1
    while (low <= high) {
        val mid = (low + high) / 2
        if (rank0[mid + 1] > j) {
            high = mid - 1
        } else if (rank0[mid + 1] < j) {
            low = mid + 1
        } else {
            if (!this[mid]) return mid.toShort()
            high = mid - 1
        }
    }
    return -1
}

fun List<Boolean>.toBitSet(): BitSet {
    val bitSet = BitSet(this.size)
    this.forEachIndexed { index, value ->
        if (value) {
            bitSet.set(index, true)
        }
    }
    return bitSet
}

fun List<Char>.toByteArrayFromListChar(): ByteArray {
    return this.map { it.code }.toByteArray()
}

fun ByteArray.toListChar(): MutableList<Char> {
    return this.toListInt().map { it.toChar() }.toMutableList()
}

fun List<Int>.toByteArray(): ByteArray {
    val buffer = ByteBuffer.allocate(this.size * 4) // Each Int occupies 4 bytes
    this.forEach { buffer.putInt(it) }
    return buffer.array()
}

fun ByteArray.toListInt(): MutableList<Int> {
    val intList = mutableListOf<Int>()
    for (i in indices step 4) {
        val value = (this[i].toInt() shl 24) or
                ((this[i + 1].toInt() and 0xFF) shl 16) or
                ((this[i + 2].toInt() and 0xFF) shl 8) or
                (this[i + 3].toInt() and 0xFF)
        intList.add(value)
    }
    return intList
}

fun List<Short>.toByteArrayFromListShort(): ByteArray {
    val byteArray = ByteArray(this.size * 2) // Each Short occupies 2 bytes
    for (i in this.indices) {
        val shortValue = this[i]
        byteArray[i * 2] = (shortValue.toInt() shr 8).toByte() // High byte
        byteArray[i * 2 + 1] = shortValue.toByte() // Low byte
    }
    return byteArray
}

fun ByteArray.byteArrayToShortList(): List<Short> {
    val shortList = mutableListOf<Short>()
    for (i in indices step 2) {
        val highByte = this[i].toInt() and 0xFF
        val lowByte = this[i + 1].toInt() and 0xFF
        val shortValue = (highByte shl 8) or lowByte
        shortList.add(shortValue.toShort())
    }
    return shortList
}

fun BitSet.toBooleanList(): List<Boolean> {
    return (0 until this.size()).map { this[it] }
}

fun List<Int>.toBitSetExtension(): BitSet {
    val bitSet = BitSet()
    this.forEach { bitSet.set(it) }
    return bitSet
}

fun BooleanArray.boolArrayToBitSet(): BitSet {
    val bitSet = BitSet(this.size)
    this.forEachIndexed { index, value ->
        if (value) {
            bitSet.set(index)
        }
    }
    return bitSet
}

fun readCharArrayFromBytes(
    inputStream: InputStream
): CharArray {
    val byteArray = inputStream.readBytes()
    val byteBuffer = ByteBuffer.wrap(byteArray)
    val charArray = CharArray(byteArray.size / 2)
    byteBuffer.asCharBuffer().get(charArray)
    return charArray
}

fun BitSet.toBooleanArray(): BooleanArray {
    return BooleanArray(this.length()) { this[it] }
}
