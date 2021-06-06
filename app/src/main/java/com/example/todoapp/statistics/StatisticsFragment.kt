package com.example.todoapp.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.todoapp.R
import com.example.todoapp.databinding.StatisticsFragBinding

/**
 * tasks统计页面主要ui
 */
class StatisticsFragment : Fragment() {
    private lateinit var viewDataBinding: StatisticsFragBinding

    private lateinit var statisticsViewModel: StatisticsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = DataBindingUtil.inflate(
            inflater, R.layout.statistics_frag, container,
            false
        )
        return viewDataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        statisticsViewModel = (activity as StatisticsActivity).obtainViewModel()
        viewDataBinding.stats = statisticsViewModel
        viewDataBinding.setLifecycleOwner(this.viewLifecycleOwner)
    }

    override fun onResume() {
        super.onResume()
        statisticsViewModel.start()
    }

    companion object {
        fun newInstance() = StatisticsFragment()
    }
}