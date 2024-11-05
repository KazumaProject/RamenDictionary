package token_array

import louds.louds.LOUDS
import rank1
import rank1Common
import select0
import select0Common
import toBitSet
import java.io.IOException
import java.io.ObjectInput
import java.io.ObjectOutput
import java.util.*

class TokenArray {
    private var nodeIdJPListTemp: MutableList<Int> = arrayListOf()
    private var nodeIdJPList: IntArray = intArrayOf()
    private var bitListTemp: MutableList<Boolean> = arrayListOf()
    var bitvector: BitSet = BitSet()

    fun nodeIdDictionaryJP(
        nodeId: Int, rank0ArrayTokenArrayBitvector: IntArray, rank1ArrayTokenArrayBitvector: IntArray
    ): Int {
        val select0 = bitvector.select0Common(nodeId, rank0ArrayTokenArrayBitvector)
        val rank1 = bitvector.rank1Common(
            select0, rank1ArrayTokenArrayBitvector
        )
        return rank1
    }

    fun valueInNodeIdJPList(index: Int): Int {
        return nodeIdJPList[index]
    }

    fun buildTokenArray(
        ramenStoreData: List<Pair<String, String>>,
        dictionaryJP: LOUDS,
        out: ObjectOutput,
    ) {
        ramenStoreData.forEachIndexed { index, pair ->
            bitListTemp.add(false)
            bitListTemp.add(true)
            val nodeId = dictionaryJP.getNodeIndex(pair.second)
            nodeIdJPListTemp.add(nodeId)
        }
        writeExternal(out)
    }

    private fun ramenJPNodeIdForDictionary(string: String, tangoTrie: LOUDS): Short {
        return tangoTrie.getNodeIndex(string).toShort()
    }

    private fun writeExternal(
        out: ObjectOutput
    ) {
        try {
            out.apply {
                writeObject(nodeIdJPListTemp.toIntArray())
                writeObject(bitListTemp.toBitSet())
                flush()
                close()
            }
        } catch (e: IOException) {
            println(e.stackTraceToString())
        }
    }

    fun readExternal(
        objectInput: ObjectInput,
    ): TokenArray {
        objectInput.apply {
            try {
                nodeIdJPList = readObject() as IntArray
                bitvector = readObject() as BitSet
                close()
            } catch (e: Exception) {
                println(e.stackTraceToString())
            }
        }
        return TokenArray()
    }
}