package com.example.eazyremote.ui.fragments

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.eazyremote.R
import com.example.eazyremote.databinding.FragmentCreateConnectionBinding
import com.example.eazyremote.di.utils.Injectable
import kotlin.math.roundToInt

class CreateConnectionFragment : Fragment(), Injectable {

    lateinit var binding : FragmentCreateConnectionBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentCreateConnectionBinding.inflate(inflater)
        binding.lifecycleOwner = this

        animateImageView()

        return binding.root
    }

    private fun animateImageView() {

        val orange = ContextCompat.getColor(requireContext(), R.color.whiteColor)
        val colorAnim = ObjectAnimator.ofFloat(0f, 1f)
        colorAnim.setIntValues(R.color.whiteColor, R.color.yellowColor)
        colorAnim.setEvaluator(ArgbEvaluator())
        colorAnim.addUpdateListener { animation ->
            val mul = animation.animatedValue as Float
            val alphaOrange = adjustAlpha(orange, mul)
            binding.logo.setColorFilter(alphaOrange, PorterDuff.Mode.SRC_ATOP)
        }
        colorAnim.currentPlayTime = 2000L
        colorAnim.duration = 2000L
        colorAnim.repeatMode = ValueAnimator.REVERSE
        colorAnim.repeatCount = 1
        colorAnim.doOnEnd {
            toMain()
        }
        colorAnim.start()
    }

    private fun toMain() {
        val bundle  = Bundle()
        bundle.putBoolean("showUI", true)
        this.findNavController().popBackStack(R.id.createConnectionFragment,
            true)
        this.findNavController().navigate(R.id.mainScreenFragment, bundle)
    }

    private fun adjustAlpha(color: Int, factor: Float): Int {
        val alpha = (Color.alpha(color) * factor).roundToInt()
        val red: Int = Color.red(color)
        val green: Int = Color.green(color)
        val blue: Int = Color.blue(color)
        return Color.argb(alpha, red, green, blue)
    }
}
