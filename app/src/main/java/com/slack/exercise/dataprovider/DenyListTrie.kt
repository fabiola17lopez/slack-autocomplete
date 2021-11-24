package com.slack.exercise.dataprovider.trie

data class TrieNode(
    val value: Char?,
    val children: MutableMap<Char, TrieNode> = mutableMapOf(),
    var isTerminating: Boolean = false
)

/**
 * Trie implementation to handle denylist checks
 */
class DenyListTrie {
    val root = TrieNode(null)

    fun insert(term: String) {
        if (term.isEmpty()) return

        var node = root
        for (character in term) {
            // for each character, add a child node if it doesn't already exist
            if (node.children[character] == null) {
                node.children[character] = TrieNode(character)
            }

            // increment the depth for the next character
            node = node.children[character]!!
        }

        // indicate that this node is the end of our string
        node.isTerminating = true
    }

    /**
     * @return true if the given term matches or starts with anything found in the trie
     */
    fun containsPrefixForTerm(term: String): Boolean {
        if (term.isEmpty()) return false

        var node = root
        for (character in term) {
            val nodeForCharacter = node.children[character]
            when {
                nodeForCharacter == null -> {
                    // no matching prefix exists for the given term
                    return false
                }
                nodeForCharacter.isTerminating -> {
                    // a matching prefix has been found for the given term
                    return true
                }
                else -> {
                    // otherwise, we go one node deeper
                    node = node.children[character]!!
                }
            }
        }

        // if we've gone through all the characters in term without finding a terminating node,
        // no matching prefix exists for term
        return false
    }
}