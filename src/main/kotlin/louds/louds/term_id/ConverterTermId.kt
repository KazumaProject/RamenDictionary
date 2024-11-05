package louds.louds.term_id

import prefix.with_term_id.PrefixNodeWithTermId
import java.util.*

class ConverterTermId {

    fun convert(
        rootNode: PrefixNodeWithTermId,
    ): LOUDSTermId {

        val louds = LOUDSTermId()
        val queue: Queue<PrefixNodeWithTermId> = ArrayDeque()
        queue.add(rootNode)

        while (!queue.isEmpty()) {
            processQueue(queue, louds)
        }
        return louds
    }

    private fun processQueue(queue: Queue<PrefixNodeWithTermId>, louds: LOUDSTermId) {
        val node: PrefixNodeWithTermId = queue.poll()
        if (node.hasChild()) {
            node.children.forEach { entry ->
                queue.add(entry.value.second)
                louds.apply {
                    LBSTemp.add(true)
                    labelsTemp.add(entry.key)
                    isLeafTemp.add(entry.value.second.isWord)
                    if (entry.value.second.isWord) {
                        termIdsTemp.add(entry.value.second.termId)
                    }
                }
            }
        }

        louds.apply {
            LBSTemp.add(false)
            isLeafTemp.add(false)
        }

    }

}