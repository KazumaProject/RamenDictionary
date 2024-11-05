package converter

import louds.louds.LOUDS
import louds.louds.term_id.LOUDSTermId
import rank0GetIntArray
import rank1GetIntArray
import toBooleanArray
import token_array.TokenArray
import java.io.ObjectInputStream

class EnglishToJapaneseConverter {
    private var dictionaryJP: LOUDS = LOUDS()
    private var dictionaryEN: LOUDSTermId = LOUDSTermId()
    private var tokenArray: TokenArray = TokenArray()
    private lateinit var auxiliaryDataDictionaryJPLBSRank0: IntArray
    private lateinit var auxiliaryDataDictionaryJPLBSRank1: IntArray
    private lateinit var auxiliaryDataDictionaryENLBSRank0: IntArray
    private lateinit var auxiliaryDataDictionaryENLBSRank1: IntArray
    private lateinit var auxiliaryDataDictionaryENLBSBooleanArray: BooleanArray
    private lateinit var auxiliaryDataDictionaryENIsLeafRank0: IntArray
    private lateinit var auxiliaryDataDictionaryENIsLeafRank1: IntArray
    private lateinit var auxiliaryDataTokenArrayLBSRank0: IntArray
    private lateinit var auxiliaryDataTokenArrayLBSRank1: IntArray
    private lateinit var auxiliaryDataDictionaryENLBSPreprocess: IntArray

    fun build(
        objectInputStreamJP: ObjectInputStream,
        objectInputStreamEN: ObjectInputStream,
        tokenArrayObjectInputStream: ObjectInputStream,
    ) {
        dictionaryEN.readExternal(objectInputStreamEN)
        dictionaryJP.readExternal(objectInputStreamJP)
        tokenArray.readExternal(tokenArrayObjectInputStream)
        auxiliaryDataDictionaryJPLBSRank0 = dictionaryJP.LBS.rank0GetIntArray()
        auxiliaryDataDictionaryJPLBSRank1 = dictionaryJP.LBS.rank1GetIntArray()
        auxiliaryDataDictionaryENLBSRank0 = dictionaryEN.LBS.rank0GetIntArray()
        auxiliaryDataDictionaryENLBSRank1 = dictionaryEN.LBS.rank1GetIntArray()
        auxiliaryDataDictionaryENLBSBooleanArray = dictionaryEN.LBS.toBooleanArray()
        auxiliaryDataDictionaryENLBSPreprocess = preprocessLBSInBoolArray(auxiliaryDataDictionaryENLBSBooleanArray)
        auxiliaryDataDictionaryENIsLeafRank0 = dictionaryEN.isLeaf.rank0GetIntArray()
        auxiliaryDataDictionaryENIsLeafRank1 = dictionaryEN.isLeaf.rank1GetIntArray()
        auxiliaryDataTokenArrayLBSRank0 = tokenArray.bitvector.rank0GetIntArray()
        auxiliaryDataTokenArrayLBSRank1 = tokenArray.bitvector.rank1GetIntArray()
    }

    fun convert(input: String): String {
        val nodeIndex = dictionaryEN.getNodeIndex(
            s = input,
            rank1Array = auxiliaryDataDictionaryENLBSRank1,
            LBSInBoolArray = auxiliaryDataDictionaryENLBSBooleanArray,
            LBSInBoolArrayPreprocess = auxiliaryDataDictionaryENLBSPreprocess
        )
        val termId = dictionaryEN.getTermId(
            nodeIndex = nodeIndex,
            rank1ArrayIsLeaf = auxiliaryDataDictionaryENIsLeafRank1
        )
        val indexInNodeIdJPList = tokenArray.nodeIdDictionaryJP(
            nodeId = termId,
            rank0ArrayTokenArrayBitvector = auxiliaryDataTokenArrayLBSRank0,
            rank1ArrayTokenArrayBitvector = auxiliaryDataTokenArrayLBSRank1
        )
        val nodeIndexDictionaryJP = tokenArray.valueInNodeIdJPList(indexInNodeIdJPList)
        return dictionaryJP.getLetter(
            nodeIndexDictionaryJP,
            auxiliaryDataDictionaryJPLBSRank0,
            auxiliaryDataDictionaryJPLBSRank1
        )
    }

    private fun preprocessLBSInBoolArray(LBSInBoolArray: BooleanArray): IntArray {
        val prefixSum = IntArray(LBSInBoolArray.size + 1)
        for (i in LBSInBoolArray.indices) {
            prefixSum[i + 1] = prefixSum[i] + if (LBSInBoolArray[i]) 0 else 1
        }
        return prefixSum
    }
}