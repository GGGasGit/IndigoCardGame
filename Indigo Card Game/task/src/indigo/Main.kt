package indigo

import kotlin.system.exitProcess
import kotlin.random.Random

object Deck {
    private val ranks = listOf("A", "2", "3", "4", "5", "6", "7", "8", "9", "T", "J", "Q", "K")
    private val suits = listOf("♦", "♥", "♠", "♣")
    private val originalDeck: List<String> = buildList {
        for (suit in suits) {
            for (rank in ranks) {
                add("$rank$suit")
            }
        }
    }
    var currentDeck = originalDeck.toMutableList()

    fun shuffle() {
        val copyDeck = currentDeck.toMutableList()
        val randGen = Random.Default
        for (i in 0 until currentDeck.lastIndex) {
            val random = randGen.nextInt(copyDeck.size - 1)
            currentDeck[i] = copyDeck[random]
            copyDeck.removeAt(random)
        }
        currentDeck[currentDeck.lastIndex] = copyDeck[0]
    }

    private fun removeCard() = currentDeck.removeAt(0)

    fun getCards(number: Int): MutableList<String> {
        val removedCards: List<String> = buildList {
            repeat(number) {
                add(removeCard())
            }
        }
        return removedCards.toMutableList()
    }
}

class Player {
    lateinit var cardsInHand: MutableList<String>
    var playedFirst = false
    var playerTurn = false
    var collectedCards = 0
    var points = 0
    var wonLast = false

    fun playCard(index: Int) {
        cardsInHand.removeAt(index)
    }

    override fun toString(): String {
        return "Cards in hand: ${cardsInHand.joinToString(" ") { "${cardsInHand.indexOf(it) + 1})${it.replace("T", "10")}" }}"
    }

    private fun selectCandidateCards(topCard: String): List<String> {
        val rankCandidates: List<String> = cardsInHand.filter { it[0] == topCard[0] }
        val suitCandidates: List<String> = cardsInHand.filter { it[1] == topCard[1] }

        return if (suitCandidates.size > 1) {
            suitCandidates
        } else if (rankCandidates.size > 1) {
            rankCandidates
        } else {
            buildList {
                addAll(rankCandidates)
                addAll(suitCandidates)
            }
        }
    }

    private fun selectCardInRandom(): Int {
        val randGen = Random.Default
        for (suit in listOf('♦', '♥', '♠', '♣')) {
            val cardsWithSameSuit = cardsInHand.filter { it[1] == suit }
            if (cardsWithSameSuit.size > 1) {
                val random = randGen.nextInt(cardsWithSameSuit.size)
                return cardsInHand.indexOf(cardsWithSameSuit[random])
            }
        }
        for (rank in listOf('A', '2', '3', '4', '5', '6', '7', '8', '9', 'T', 'J', 'Q', 'K')) {
            val cardsWithSameRank = cardsInHand.filter { it[0] == rank }
            if (cardsWithSameRank.size > 1) {
                val random = randGen.nextInt(cardsWithSameRank.size)
                return cardsInHand.indexOf(cardsWithSameRank[random])
            }
        }
        val random = randGen.nextInt(cardsInHand.size)
        return cardsInHand.indexOf(cardsInHand[random])
    }

    fun selectCardToPlay(deckOnTable: MutableList<String>): Int {
        if (cardsInHand.size == 1) {
            return 0
        } else {
            return if (deckOnTable.size == 0) {
                selectCardInRandom()
            } else {
                val candidateCards = selectCandidateCards(deckOnTable.last())
                if (candidateCards.isEmpty()) {
                    selectCardInRandom()
                } else if (candidateCards.size == 1) {
                    cardsInHand.indexOf(candidateCards[0])
                } else {
                    for (rank in listOf('A', 'T', 'J', 'Q', 'K')) {
                        val scoringCards = candidateCards.filter { it[0] == rank }
                        if (scoringCards.isNotEmpty()) {
                            cardsInHand.indexOf(scoringCards[0])
                        }
                    }
                    val randGen = Random.Default
                    val random = randGen.nextInt(candidateCards.size)
                    cardsInHand.indexOf(candidateCards[random])
                }
            }
        }
    }

}

class Game(private val deck: Deck, private val player: Player, private val computer: Player) {
    private var deckOnTable: MutableList<String>
    init {
        deck.shuffle()
        println("Indigo Card Game")
        while (true) {
            println("Play first?")
            val answer = readln().lowercase()
            if (answer == "exit") exitProcess(0)
            if (answer == "yes") {
                player.playedFirst = true
                player.playerTurn = true
                player.cardsInHand = deck.getCards(6)
                computer.cardsInHand = deck.getCards(6)
                break
            }
            if (answer == "no") {
                computer.playerTurn = true
                computer.cardsInHand = deck.getCards(6)
                player.cardsInHand = deck.getCards(6)
                break
            }
        }
        deckOnTable = deck.getCards(4)
        println("Initial cards on the table: ${deckOnTable.joinToString(" ") { it.replace("T", "10") } }")
    }

    private fun checkCardsMatch(playedCard: String, topCard: String): Boolean =
        playedCard[0] == topCard[0] || playedCard[1] == topCard[1]

    private fun givePoints(player: Player) {
        player.collectedCards += deckOnTable.size
        player.points += deckOnTable.filter { it[0] == 'A' || it[0] == 'K' || it[0] == 'Q' || it[0] == 'J' || it[0] == 'T' }.size
        deckOnTable.clear()
    }

    private fun printPoints() {
        println("Score: Player ${player.points} - Computer ${computer.points}")
        println("Cards: Player ${player.collectedCards} - Computer ${computer.collectedCards}")
    }

    fun play() {
        while (true) {

            println(if (deckOnTable.size == 0) "\nNo cards on the table" else
                "\n${deckOnTable.size} cards on the table, and the top card is ${deckOnTable.last().replace("T", "10")}")

            if (deck.currentDeck.size == 0 && player.cardsInHand.size == 0 && computer.cardsInHand.size == 0) {
                if (deckOnTable.size > 0) {
                    if (!player.wonLast && !computer.wonLast) {
                        givePoints(if (player.playedFirst) player else computer)
                    } else {
                        givePoints(if (player.wonLast) player else computer)
                    }
                }
                if (player.collectedCards > computer.collectedCards) {
                    player.points += 3
                } else if (player.collectedCards < computer.collectedCards) {
                    computer.points += 3
                } else {
                    if (player.playedFirst) {
                        player.points += 3
                    } else {
                        computer.points += 3
                    }
                }
                printPoints()
                println("Game Over")
                break

            } else {

                if (player.playerTurn) {
                    println(player)
                    while (true) {
                        println("Choose a card to play (1-${player.cardsInHand.size}):")
                        val input = readln()
                        if (input == "exit") {
                            println("Game Over")
                            exitProcess(0)
                        }
                        if (input.matches(Regex("\\d")) && input.toInt() in 1..player.cardsInHand.size) {
                            deckOnTable.add(player.cardsInHand[input.toInt() - 1])
                            player.playCard(input.toInt() - 1)
                            if (deckOnTable.size > 1 && checkCardsMatch(deckOnTable[deckOnTable.lastIndex - 1], deckOnTable.last())) {
                                givePoints(player)
                                player.wonLast = true
                                computer.wonLast = false
                                println("Player wins cards")
                                printPoints()
                            }
                            break
                        }
                    }
                    if (player.cardsInHand.size == 0 && deck.currentDeck.size >= 6) player.cardsInHand = deck.getCards(6)
                    player.playerTurn = false
                } else {
                    println(computer.cardsInHand.joinToString(" ") { it.replace("T", "10") })
                    val cardIndex = computer.selectCardToPlay(deckOnTable)
                    println("Computer plays ${computer.cardsInHand[cardIndex].replace("T", "10")}")
                    deckOnTable.add(computer.cardsInHand[cardIndex])
                    computer.playCard(cardIndex)
                    if (deckOnTable.size > 1 && checkCardsMatch(deckOnTable[deckOnTable.lastIndex - 1], deckOnTable.last())) {
                        givePoints(computer)
                        computer.wonLast = true
                        player.wonLast = false
                        println("Computer wins cards")
                        printPoints()
                    }
                    if (computer.cardsInHand.size == 0 && deck.currentDeck.size >= 6) computer.cardsInHand = deck.getCards(6)
                    player.playerTurn = true
                }
            }
        }
    }

}

fun main() {
    val deck = Deck
    val player = Player()
    val computer = Player()
    val game = Game(deck, player, computer)
    game.play()
}