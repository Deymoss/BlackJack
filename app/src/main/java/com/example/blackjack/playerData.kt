package com.example.blackjack

import android.widget.ImageView

data class Player(val name:String = "") {
    var score = 0
    var hBias = 0.460F
    var diceHBias = 0.500F
    var drawedCards = 0
    var swich = 0
    var cardViews = mutableListOf<ImageView>()
    var cards = mutableListOf<Int>()
    var blackCheck = false
    var betValue = 0
    var amountOfCash = 5000
}
fun Player.reset() {
    score = 0
    hBias = 0.460F
    diceHBias = 0.500F
    drawedCards = 0
    swich = 0
    cards.clear()
    blackCheck = false
    betValue = 0
}
data class Dealer(val name:String = "") {
    var cards = mutableListOf<Int>()
    var cardViews = mutableListOf<ImageView>()
    var firstCard = mutableMapOf<String, Int>()
    var blackCheck = false
    var hBias = 0.460F
    var vBias = 0.120F
    var score = 0
    var drawedCards = 0
}
fun Dealer.reset() {
    score = 0
    hBias = 0.460F
    vBias = 0.120F
    firstCard.clear()
    cardViews.clear()
    cards.clear()
    drawedCards = 0
    blackCheck = false
}