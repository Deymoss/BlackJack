package com.example.blackjack

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.transition.doOnEnd
import com.example.blackjack.databinding.ActivityMainBinding
import kotlin.collections.shuffled
import android.graphics.Shader

import android.graphics.LinearGradient

import android.text.TextPaint
import androidx.core.view.isVisible
import kotlin.system.exitProcess


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var player: Player = Player()
    private var splitPlayer: Player = Player()
    private var data: WholeData = WholeData()
    private var dealer: Dealer = Dealer()
    var animState = "bet"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val paint: TextPaint = binding.Cash.paint
        val width = paint.measureText("5000$")

        val textShader: Shader = LinearGradient(
            0F, 0F, width, binding.Cash.textSize, intArrayOf(
                Color.parseColor("#F97C3C"),
                Color.parseColor("#FDB54E"),
                Color.parseColor("#64B678"),
                Color.parseColor("#478AEA"),
                Color.parseColor("#8446CC")
            ), null, Shader.TileMode.CLAMP
        )
        binding.Cash.paint.shader = textShader
        binding.Cash.setTextColor(Color.parseColor("#F97C3C"))
        binding.apply {
            nextButton.isVisible = false
            exitButton.isVisible = false
            hitButton.isEnabled = false
            standButton.isEnabled = false
            doubleButton.isEnabled = false
            splitButton.isEnabled = false
            dice50Button.setOnClickListener {
                addBet(R.drawable._50dice, 50)
            }
            dice100Button.setOnClickListener {
                addBet(R.drawable._100dice, 100)
            }
            dice500Button.setOnClickListener {
                addBet(R.drawable._500dice, 500)
            }
            dice10Button.setOnClickListener {
                addBet(R.drawable._10dice, 10)
            }
            dice1000Button.setOnClickListener {
                addBet(R.drawable._1000dice, 1000)
            }
            betButton.setOnClickListener {
                    betThis()
            }
        }
    }

    private fun rotateAnimating(view: ImageView, ResId: Int, dealerTurn: Boolean, player: Player) {
        ConstraintSet().apply {
            clone(binding.myLayout)
            if(dealerTurn) {
                setVerticalBias(view.id, dealer.vBias)
                setHorizontalBias(view.id, dealer.hBias)
            } else {
                setVerticalBias(view.id, 0.570F)
                setHorizontalBias(view.id, player.hBias)
            }
            val transition = AutoTransition()
            transition.duration = 700
            transition.interpolator = AccelerateDecelerateInterpolator()
            transition.doOnEnd{
                if(animState == "betThis") {
                    hitPressed(false, player)
                } else if(dealerTurn && dealer.drawedCards<2) {
                    hitPressed(true, player)
                } else if(!dealerTurn && player.drawedCards == 2)
                {
                    hitPressed(true, player)
                } else if(dealerTurn && dealer.drawedCards > 2 && dealer.score <17) {
                    hitPressed(true, player)
                }
                    animState = " "
                 }
            TransitionManager.beginDelayedTransition(binding.myLayout, transition)
            applyTo(binding.myLayout)
        }
        if(dealerTurn && dealer.drawedCards == 1) {
        } else {
            view.animate().withLayer()
                .rotationY(90F)
                .setDuration(500)
                .withEndAction {
                    run {

                        view.setImageResource(ResId)

                        // second quarter turn
                        view.rotationY = -90F
                        view.animate().withLayer()
                            .rotationY(0F)
                            .setDuration(500)
                            .start()
                    }
                }.start()
        }

    }

    private fun giveCard(): Map.Entry<String, Int> {
        return data.deck.entries.shuffled().first()
    }

    private fun hitPressed(dealerTurn: Boolean, player: Player) {
            val newView = ImageView(this)
            binding.myLayout.addView(newView)
            val dp = newView.context.resources.displayMetrics.density
            newView.layoutParams.height = (99 * dp).toInt()
            newView.layoutParams.width = (60 * dp).toInt()
            newView.id = View.generateViewId()
            data.cardViews.add(newView)

        if(dealerTurn && dealer.drawedCards == 1) {
            dealer.hBias += 0.090F
            dealer.drawedCards += 1
            dealer.cardViews.add(newView)
        }else if(!dealerTurn) {
            player.hBias += 0.02F
            player.drawedCards += 1
            player.cardViews.add(newView)
        } else {
            dealer.cardViews.add(newView)
            dealer.hBias += 0.02F
            dealer.drawedCards += 1
        }
            ConstraintSet().apply {
            clone(binding.myLayout)
            connect(newView.id, ConstraintSet.BOTTOM, binding.myLayout.id, ConstraintSet.BOTTOM)
            connect(newView.id, ConstraintSet.TOP, binding.myLayout.id, ConstraintSet.TOP)
            connect(newView.id, ConstraintSet.LEFT, binding.myLayout.id, ConstraintSet.LEFT)
            connect(newView.id, ConstraintSet.RIGHT, binding.myLayout.id, ConstraintSet.RIGHT)
            setHorizontalBias(newView.id, 0.047F)
            setVerticalBias(newView.id, 0.051F)
            applyTo(binding.myLayout)
            }
            newView.setImageResource(R.drawable.gray_back)
            val card = giveCard()
            if(dealerTurn) {
                if(dealer.cards.size < 1)
                    dealer.firstCard[card.key] = card.value
            binding.myLayout.post {
                  rotateAnimating(
                      newView,
                        this.resources.getIdentifier(card.key, "drawable", this.packageName),
                     true, player)
             }
            } else {
                binding.myLayout.post {
                    rotateAnimating(
                        newView,
                        this.resources.getIdentifier(card.key, "drawable", this.packageName),
                        false, player)
                }
            }
            countingPlayerScore(card, dealerTurn)
            if(player.score > 21)
            {
                stand()
            }
    }

    private fun countingPlayerScore(card: Map.Entry<String, Int>, dealerTurn: Boolean){
        if(dealerTurn) {
            if(card.key.startsWith("_a"))
            {
                if(dealer.score < 11) {
                    if(dealer.score == 10 && dealer.cards.size == 1) {
                        dealer.score += 11
                        dealer.blackCheck = true
                    } else {
                        dealer.score += 11
                        dealer.cards.add(11)
                    }
                } else {
                    dealer.score += 1
                    dealer.cards.add(1)
                }
            } else {
                dealer.score += card.value
                dealer.cards.add(card.value)
            }
            if(dealer.score + card.value >21 && dealer.cards.contains(11))
            {
                dealer.cards.remove(11)
                dealer.cards.add(1)
                dealer.score = dealer.cards.sum()
            }
        } else {
            println("fuck")
            if (card.key.startsWith("_a")) {
                if (player.score < 11) {
                    if (player.score == 10 && player.cards.size == 1) {
                        player.score += 11
                        player.blackCheck = true
                    } else {
                        player.score += 11
                        player.cards.add(11)
                    }
                } else {
                    player.score += 1
                    player.cards.add(1)
                }
            } else {
                player.score += card.value
                player.cards.add(card.value)
            }
            if (player.score + card.value > 21 && player.cards.contains(11)) {
                player.cards.remove(11)
                player.cards.add(1)
                player.cards.sort()
                player.score = player.cards.sum()
            }
        }
    }

    private fun addBet(resId: Int, amount: Int) {
        binding.cancelButton.setOnClickListener {
            cancelBet()
        }
        if(player.amountOfCash - player.betValue >= amount) {
            val newView = ImageView(this)
            binding.myLayout.addView(newView)
            val dp = newView.context.resources.displayMetrics.density
            newView.layoutParams.height = (50 * dp).toInt()
            newView.layoutParams.width = (50 * dp).toInt()
            newView.id = View.generateViewId()
            data.diceViews.add(newView)
            ConstraintSet().apply {
                clone(binding.myLayout)
                connect(newView.id, ConstraintSet.BOTTOM, binding.myLayout.id, ConstraintSet.BOTTOM)
                connect(newView.id, ConstraintSet.TOP, binding.myLayout.id, ConstraintSet.TOP)
                connect(newView.id, ConstraintSet.LEFT, binding.myLayout.id, ConstraintSet.LEFT)
                connect(newView.id, ConstraintSet.RIGHT, binding.myLayout.id, ConstraintSet.RIGHT)
                setHorizontalBias(newView.id, player.diceHBias)
                setVerticalBias(newView.id, 0.876F)
                newView.setImageResource(resId)
                applyTo(binding.myLayout)
            }
            if(player.diceHBias < 0.590)
            player.diceHBias += 0.01F
            player.betValue += amount
            binding.betAmount.text = player.betValue.toString()
        } else {
            val toast = Toast.makeText(applicationContext, "недостаточно средств", Toast.LENGTH_SHORT)
            toast.show()
        }
    }
    private fun betThis() {
        if(player.betValue != 0) {
            animState = "betThis"
            hitPressed(false, player)
            binding.hitButton.setOnClickListener {
                if(data.isSplited && data.currentSplit == 0) {
                    hitPressed(false, player)
                } else if(data.isSplited && data.currentSplit == 1)
                {
                    hitPressed(false, splitPlayer)
                } else {
                    hitPressed(false, player)
                }
            }
            player.amountOfCash -= player.betValue
            binding.Cash.text = player.amountOfCash.toString() + "$"
            onStartGame()
        }
    }

    private fun cancelBet() {
        player.amountOfCash += player.betValue
        player.betValue = 0
        binding.betAmount.text = player.betValue.toString()
        for(i in data.diceViews)
        {
            binding.myLayout.removeView(i)
        }
        data.diceViews.clear()
    }

    private fun split() {
        if(player.betValue <= player.amountOfCash) {
            splitPlayer.hBias = player.hBias+0.1F
            val newView = TextView(this)
            binding.myLayout.addView(newView)
            newView.layoutParams.height = ConstraintSet.WRAP_CONTENT
            newView.layoutParams.width = ConstraintSet.WRAP_CONTENT
            newView.id = View.generateViewId()
            newView.text = player.betValue.toString()
            newView.textSize = 18F
            newView.setTextColor(binding.betAmount.textColors)
            data.textViews.add(newView)
            ConstraintSet().apply {
                clone(binding.myLayout)
                connect(newView.id, ConstraintSet.BOTTOM, binding.myLayout.id, ConstraintSet.BOTTOM)
                connect(newView.id, ConstraintSet.TOP, binding.myLayout.id, ConstraintSet.TOP)
                connect(newView.id, ConstraintSet.LEFT, binding.myLayout.id, ConstraintSet.LEFT)
                connect(newView.id, ConstraintSet.RIGHT, binding.myLayout.id, ConstraintSet.RIGHT)
                setHorizontalBias(newView.id, 0.590F)
                setVerticalBias(newView.id, 0.726F)
                    setHorizontalBias(player.cardViews[0].id, player.hBias - 0.1F)
                    setHorizontalBias(player.cardViews[1].id, player.hBias + 0.1F)
                    setHorizontalBias(binding.betAmount.id, player.hBias + 0.04F)
                val transition = AutoTransition()
                transition.duration = 700
                transition.interpolator = AccelerateDecelerateInterpolator()
                TransitionManager.beginDelayedTransition(binding.myLayout, transition)
                applyTo(binding.myLayout)
            }
            player.hBias = player.hBias - 0.1F
            splitPlayer.cardViews.add(player.cardViews[1])
            player.cardViews.removeAt(1)
            splitPlayer.cards.add(player.cards[1])
            player.cards.removeAt(1)
            data.isSplited = true
        }
    }

    private fun stand() {
        binding.apply {
            hitButton.isEnabled = false
            standButton.isEnabled = false
            doubleButton.isEnabled = false
            splitButton.isEnabled = false
        }
        if(data.isSplited && data.currentSplit == 0) {
            data.currentSplit = 1
            hitPressed(false, splitPlayer)
            binding.hitButton.isEnabled = true
            binding.standButton.isEnabled = true
            binding.doubleButton.isEnabled = true
        } else {
            dealer.cardViews[0].apply {
                animate().withLayer()
                    .rotationY(90F)
                    .setDuration(200)
                    .withEndAction {
                        run {

                            setImageResource(this.resources.getIdentifier(dealer.firstCard.entries.first().key, "drawable", packageName))

                            // second quarter turn
                           rotationY = -90F
                            animate().withLayer()
                                .rotationY(0F)
                                .setDuration(200)
                                .start()
                        }
                    }.start()
            }
            if(dealer.cards.sum() < 17) {
                hitPressed(true, player)
            }
            endGame()
        }

    }
    private fun double() {
        if(player.amountOfCash > player.betValue * 2) {
            binding.apply {
                hitButton.isEnabled = false
                standButton.isEnabled = false
                doubleButton.isEnabled = false
                splitButton.isEnabled = false
            }
            player.betValue *=2
            hitPressed(false, player)
            stand()
        } else {
            Toast.makeText(applicationContext, "недостаточно средств", Toast.LENGTH_SHORT).show()
        }
    }
    private fun onStartGame() {
        binding.apply {
            dice1000Button.isEnabled = false
            dice100Button.isEnabled = false
            dice10Button.isEnabled = false
            dice500Button.isEnabled = false
            dice50Button.isEnabled = false
            betButton.isEnabled = false
            cancelButton.isEnabled = false
            hitButton.isEnabled = true
            standButton.isEnabled = true
            doubleButton.isEnabled = true
            splitButton.isEnabled = true
        }

            binding.splitButton.setOnClickListener {
                if(player.cards.size == 2 && player.cards[0] == player.cards[1]) {
                    split()
                }
            }
        binding.doubleButton.setOnClickListener {
            double()
        }
        binding.standButton.setOnClickListener {
            stand()
        }
    }
    private fun endGame() {
        if((player.score == 21 && player.cards[0] == 10 && player.cards[1] == 11) || (player.score == 21 && player.cards[0] == 11 && player.cards[1] == 10)) {
            player.blackCheck = true
            player.amountOfCash += player.betValue * 3
            data.winstat.add(1)
            Toast.makeText(applicationContext, "Вы победили, EZ", Toast.LENGTH_SHORT).show()
        } else if((dealer.score == 21 && dealer.cards[0] == 10 && dealer.cards[1] == 11) || (dealer.score == 21 && dealer.cards[0] == 11 && dealer.cards[1] == 10)) {
            dealer.blackCheck = true
            data.winstat.add(0)
            Toast.makeText(applicationContext, "Вы проиграли.", Toast.LENGTH_SHORT).show()
        } else if(player.score == dealer.score || (player.score > 21 && dealer.score > 21)) {
            data.winstat.add(0)
            player.amountOfCash += player.betValue
            println(player.betValue)
            Toast.makeText(applicationContext, "Ничья!", Toast.LENGTH_SHORT).show()
        } else if(player.score > 21 && dealer.score <= 21) {
            data.winstat.add(0)
            Toast.makeText(applicationContext, "Вы проиграли.", Toast.LENGTH_SHORT).show()
        } else if(dealer.score > 21 && player.score <= 21) {
            data.winstat.add(1)
            player.amountOfCash += player.betValue * 2
            Toast.makeText(applicationContext, "Вы победили, EZ", Toast.LENGTH_SHORT).show()
        } else if(player.score > dealer.score) {
            data.winstat.add(1)
            player.amountOfCash += player.betValue * 2
            Toast.makeText(applicationContext, "Вы победили, EZ", Toast.LENGTH_SHORT).show()
        } else if(player.score < dealer.score) {
            data.winstat.add(0)
            Toast.makeText(applicationContext, "Вы проиграли.", Toast.LENGTH_SHORT).show()
        }
        binding.apply {
            Cash.text = player.amountOfCash.toString() + "$"
            nextButton.isVisible = true
            exitButton.isVisible = true
            nextButton.setOnClickListener {
                cleanAll()
                binding.apply {
                    dice1000Button.isEnabled = true
                    dice100Button.isEnabled = true
                    dice10Button.isEnabled = true
                    dice500Button.isEnabled = true
                    dice50Button.isEnabled = true
                    betButton.isEnabled = true
                    cancelButton.isEnabled = true
                    nextButton.isVisible = false
                    exitButton.isVisible = false
                }
            }
            exitButton.setOnClickListener {
                finish()
                exitProcess(0)
            }
        }
    }

    private fun cleanAll() {
        for(i in data.diceViews) {
            binding.myLayout.removeView(i)
        }
        for(i in data.cardViews) {
            binding.myLayout.removeView(i)
        }
        if(data.textViews.isNotEmpty()) {
            for(i in data.textViews) {
                binding.myLayout.removeView(i)
            }
        }
        dealer.reset()
        player.reset()
        splitPlayer.reset()
        data.reset()
        binding.betAmount.text = ""
    }
}
