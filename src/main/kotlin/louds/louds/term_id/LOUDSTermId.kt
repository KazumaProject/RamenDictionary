package louds.louds.term_id

import rank0
import rank1
import rank1Common
import rank1CommonShort
import select0
import select0Common
import select1
import toBitSet
import java.io.*
import java.util.*

class LOUDSTermId {

    val LBSTemp: MutableList<Boolean> = arrayListOf()
    var LBS: BitSet = BitSet()
    var labelsTemp: MutableList<Char> = arrayListOf()
    var labels: CharArray = charArrayOf()
    var termIdsTemp: MutableList<Int> = arrayListOf()
    var termIds: IntArray = intArrayOf()
    var isLeaf: BitSet = BitSet()
    val isLeafTemp: MutableList<Boolean> = arrayListOf()

    init {
        LBSTemp.apply {
            add(true)
            add(false)
        }
        labelsTemp.apply {
            add(0, ' ')
            add(1, ' ')
        }
        isLeafTemp.apply {
            add(0, false)
            add(1, false)
        }
    }

    constructor()


    constructor(
        LBS: BitSet,
        labels: CharArray,
        isLeaf: BitSet,
        termIds: IntArray,
    ) {
        this.LBS = LBS
        this.labels = labels
        this.isLeaf = isLeaf
        this.termIds = termIds
    }

    fun convertListToBitSet() {
        LBS = LBSTemp.toBitSet()
        LBSTemp.clear()
        isLeaf = isLeafTemp.toBitSet()
        isLeafTemp.clear()
    }

    fun getTermId(
        nodeIndex: Int,
        rank1ArrayIsLeaf: IntArray
    ): Int {
        val firstNodeId: Int = isLeaf.rank1Common(nodeIndex, rank1ArrayIsLeaf) - 1
        if (firstNodeId < 0) return -1
        return termIds[firstNodeId]
    }

    fun getNodeIndex(
        s: String,
        rank1Array: IntArray,
        LBSInBoolArray: BooleanArray,
        LBSInBoolArrayPreprocess: IntArray
    ): Int {
        return search(2, s.toCharArray(), 0, rank1Array, LBSInBoolArray, LBSInBoolArrayPreprocess)
    }

    private fun firstChild(pos: Int): Int {
        LBS.apply {
            val y = select0(rank1(pos)) + 1
            return if (!this[y]) -1 else y
        }
    }

    private fun select0CommonCached(pos: Int, rank0Array: IntArray): Int {
        val cachedResults = mutableMapOf<Pair<Int, IntArray>, Int>()
        return cachedResults.getOrPut(Pair(pos, rank0Array)) {
            LBS.select0Common(pos, rank0Array)
        }
    }

    private fun firstChild(pos: Int, rank0Array: IntArray, rank1Array: IntArray): Int {
        val y = select0CommonCached(LBS.rank1Common(pos, rank1Array), rank0Array) + 1
        return if (y < 0 || !LBS[y]) -1 else y
    }

    private fun traverse(pos: Int, c: Char): Int {
        var childPos = firstChild(pos)
        if (childPos == -1) return -1
        while (LBS[childPos]) {
            if (c == labels[LBS.rank1(childPos)]) {
                return childPos
            }
            childPos += 1
        }
        return -1
    }

    private fun traverse(pos: Int, c: Char, rank0Array: IntArray, rank1Array: IntArray): Int {
        var childPos = firstChild(pos, rank0Array, rank1Array)
        while (childPos >= 0 && LBS[childPos]) {
            if (c == labels[LBS.rank1Common(childPos, rank1Array)]) {
                return childPos
            }
            childPos++
        }
        return -1
    }

    fun commonPrefixSearch(str: String): List<String> {
        val resultTemp: MutableList<Char> = mutableListOf()
        val result: MutableList<String> = mutableListOf()
        var n = 0
        str.forEachIndexed { _, c ->
            n = traverse(n, c)
            val index = LBS.rank1(n)
            if (n == -1) return@forEachIndexed
            if (index >= labels.size) return result
            resultTemp.add(labels[index])
            if (isLeaf[n]) {
                val tempStr = resultTemp.joinToString("")
                if (result.size >= 1) {
                    val resultStr = result[0] + tempStr
                    result.add(resultStr)
                } else {
                    result.add(tempStr)
                    resultTemp.clear()
                }
            }
        }
        return result.toList()
    }

    private fun collectWords(
        pos: Int,
        prefix: StringBuilder,
        rank0Array: IntArray,
        rank1Array: IntArray,
        result: MutableList<String>
    ) {
        if (isLeaf[pos]) {
            result.add(prefix.toString())
        }
        var childPos = firstChild(pos, rank0Array, rank1Array)
        while (childPos >= 0 && LBS[childPos]) {
            val index = LBS.rank1Common(childPos, rank1Array)
            if (index >= labels.size) break
            prefix.append(labels[index])
            collectWords(childPos, prefix, rank0Array, rank1Array, result)
            prefix.deleteCharAt(prefix.length - 1)
            childPos++
        }
    }

    fun predictiveSearch(prefix: String, rank0Array: IntArray, rank1Array: IntArray): List<String> {
        val result = mutableListOf<String>()
        val resultTemp = StringBuilder()
        var n = 0
        for (c in prefix) {
            n = traverse(n, c, rank0Array, rank1Array)
            if (n < 0) return result
            val index = LBS.rank1Common(n, rank1Array)
            if (index >= labels.size) return result
            resultTemp.append(labels[index])
        }
        collectWords(n, resultTemp, rank0Array, rank1Array, result)
        return result
    }

    private tailrec fun search(
        index: Int,
        chars: CharArray,
        wordOffset: Int,
        rank1Array: IntArray,
        LBSInBoolArray: BooleanArray,
        LBSInBoolArrayPreprocess: IntArray
    ): Int {
        var currentIndex = index
        var charIndex = LBS.rank1Common(currentIndex, rank1Array)
        val charCount = chars.size

        while (currentIndex < LBSInBoolArray.size && LBSInBoolArray[currentIndex]) {
            val currentChar = chars[wordOffset]
            val currentLabel = labels[charIndex]

            if (currentChar == currentLabel) {
                if (wordOffset + 1 == charCount) {
                    return if (isLeaf[currentIndex]) currentIndex else currentIndex
                }
                val nextIndex = indexOfLabel(charIndex, LBSInBoolArrayPreprocess)
                return search(
                    nextIndex,
                    chars,
                    wordOffset + 1,
                    rank1Array,
                    LBSInBoolArray,
                    LBSInBoolArrayPreprocess
                )
            }
            currentIndex++
            charIndex++
        }
        return -1
    }

    private fun indexOfLabel(label: Int, prefixSum: IntArray): Int {
        var low = 0
        var high = prefixSum.size - 1

        while (low < high) {
            val mid = (low + high) / 2
            if (prefixSum[mid] < label) {
                low = mid + 1
            } else {
                high = mid
            }
        }
        return low
    }

    fun writeExternal(out: ObjectOutput) {
        try {
            out.apply {
                writeObject(LBS)
                writeObject(isLeaf)
                writeObject(labelsTemp.toCharArray())
                writeObject(termIdsTemp.toIntArray())
                flush()
                close()
            }
        } catch (e: IOException) {
            println(e.stackTraceToString())
        }
    }

    fun readExternal(objectInput: ObjectInput): LOUDSTermId {
        objectInput.apply {
            try {
                LBS = objectInput.readObject() as BitSet
                isLeaf = objectInput.readObject() as BitSet
                labels = (objectInput.readObject() as CharArray)
                termIds = (objectInput.readObject() as IntArray)
                close()
            } catch (e: Exception) {
                println(e.stackTraceToString())
            }
        }
        return LOUDSTermId(LBS, labels, isLeaf, termIds)
    }

}