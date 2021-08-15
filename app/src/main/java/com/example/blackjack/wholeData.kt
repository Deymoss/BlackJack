package com.example.blackjack

import android.widget.ImageView
import android.widget.TextView

data class WholeData(val name:String = "") {
    var cardViews = mutableListOf<ImageView>()
    var diceViews = mutableListOf<ImageView>()
    var textViews = mutableListOf<TextView>()
    var isSplited = false
    var winstat = mutableListOf<Int>()
    val winSound = listOf<Int>(2, 7, 8, 11, 13, 9)
    var loseSound = listOf<Int>(5, 14, 15)
    val specialLose = listOf<Int>(4, 10)
    var currentSplit = 0
    val deck = mapOf("_2c" to 2,"_2d" to 2,"_2h" to 2,"_2s" to 2,"_3c" to 3,"_3d" to 3,"_3h" to 3,"_3s" to 3,
        "_4c" to 4,"_4d" to 4,"_4h" to 4,"_4s" to 4,"_5c" to 5,"_5d" to 5,"_5h" to 5,"_5s" to 5,
        "_6c" to 6,"_6d" to 6,"_6h" to 6,"_6s" to 6,"_7c" to 7,"_7d" to 7,"_7h" to 7,"_7s" to 7,
        "_8c" to 8,"_8d" to 8,"_8h" to 8,"_8s" to 8,"_9c" to 9,"_9d" to 9,"_9h" to 9,"_9s" to 9,
        "_10c" to 10,"_10d" to 10,"_10h" to 10,"_10s" to 10,"_jc" to 10,"_jd" to 10,"_jh" to 10,
        "_js" to 10,"_qc" to 10,"_qd" to 10,"_qh" to 10,"_qs" to 10,"_kc" to 10,"_kd" to 10,
        "_kh" to 10,"_ks" to 10,"_ac" to 11,"_ad" to 11,"_ah" to 11,"_as" to 11)
}
fun WholeData.reset() {
    cardViews.clear()
    diceViews.clear()
    textViews.clear()
    isSplited = false
    currentSplit = 0
}