package com.slack.exercise.dataprovider

import com.slack.exercise.dataprovider.trie.DenyListTrie
import com.slack.exercise.dataprovider.trie.TrieNode
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Before

class DenyListTrieTest {

    lateinit var expectedTrie: TrieNode

    @Before
    fun setup() {
        expectedTrie = TrieNode(
            value = null,
            children = mutableMapOf(
                'a' to TrieNode(
                    value = 'a',
                    children = mutableMapOf(
                        'b' to TrieNode(
                            value = 'b',
                            children = mutableMapOf(
                                'c' to TrieNode(
                                    value = 'c',
                                    isTerminating = true
                                )
                            )
                        )
                    )
                )
            )
        )
    }

    @Test
    fun `can insert independent strings into trie`() {
        val trie = DenyListTrie()

        // insert first string
        trie.insert("abc")
        assertEquals(
            expectedTrie,
            trie.root
        )

        // insert non-overlapping string
        expectedTrie.apply {
            children['d'] = TrieNode('d', isTerminating = true)
        }

        trie.insert("d")
        assertEquals(
            expectedTrie,
            trie.root
        )

        // insert duplicate should do nothing
        trie.insert("d")
        assertEquals(
            expectedTrie,
            trie.root
        )

        // insert empty string should do nothing
        trie.insert("")
        assertEquals(
            expectedTrie,
            trie.root
        )
    }

    @Test
    fun `can insert overlapping strings into trie`() {
        val trie = DenyListTrie()

        // insert first string
        trie.insert("abc")
        assertEquals(
            expectedTrie,
            trie.root
        )

        // insert prefix to existing string
        expectedTrie.children['a']?.let {
            it.isTerminating = true
        }

        trie.insert("a")
        assertEquals(
            expectedTrie,
            trie.root
        )

        // insert branch to existing string
        expectedTrie.children['a']?.let {
            it.children['z'] = TrieNode('z', isTerminating = true)
        }

        trie.insert("az")
        assertEquals(
            expectedTrie,
            trie.root
        )

        // insert extending string
        expectedTrie.children['a']?.let { aNode ->
            aNode.children['z']?.let {
                it.children['x'] = TrieNode('x', isTerminating = true)
            }
        }

        trie.insert("azx")
        assertEquals(
            expectedTrie,
            trie.root
        )
    }

    @Test
    fun `can find matches and prefixes`() {
        val trie = DenyListTrie()
        trie.insert("abc")
        trie.insert("d")
        trie.insert("az")

        // exact matches
        assertEquals(true, trie.containsPrefixForTerm("abc"))
        assertEquals(true, trie.containsPrefixForTerm("d"))
        assertEquals(true, trie.containsPrefixForTerm("az"))

        // prefixes for given term exist in trie (term is longer than trie value)
        assertEquals(true, trie.containsPrefixForTerm("abcd"))
        assertEquals(true, trie.containsPrefixForTerm("abcde"))
        assertEquals(true, trie.containsPrefixForTerm("defg"))

        // string exists in trie that starts with given term (term is shorter than trie value)
        assertEquals(false, trie.containsPrefixForTerm("a"))
        assertEquals(false, trie.containsPrefixForTerm("ab"))

        // value not found in trie
        assertEquals(false, trie.containsPrefixForTerm("b"))
        assertEquals(false, trie.containsPrefixForTerm("z"))
        assertEquals(false, trie.containsPrefixForTerm("bc"))
        assertEquals(false, trie.containsPrefixForTerm(""))
    }
}