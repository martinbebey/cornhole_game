package com.gc.baggoid.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import com.gc.baggoid.R
import com.gc.baggoid.databinding.ActivityRulesBinding

class RulesActivity : AppCompatActivity(R.layout.activity_rules) {

    private val binding: ActivityRulesBinding by lazy { ActivityRulesBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupToolbar()
    }

    private fun setupToolbar() {
        binding.toolbar.setTitle(R.string.rules)
        binding.toolbar.navigationIcon =
            AppCompatResources.getDrawable(this, R.drawable.ic_arrow_back_white_24dp)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }
}
