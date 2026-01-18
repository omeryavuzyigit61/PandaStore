package com.allmoviedatabase.pandastore.view.fragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.allmoviedatabase.pandastore.R
import com.allmoviedatabase.pandastore.adapter.OrderAdapter
import com.allmoviedatabase.pandastore.databinding.FragmentOrderListBinding
import com.allmoviedatabase.pandastore.viewmodel.OrderViewModel
import com.allmoviedatabase.pandastore.viewmodel.TimeFilter
import com.allmoviedatabase.pandastore.viewmodel.OrderListState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderListFragment : Fragment(R.layout.fragment_order_list) {

    private lateinit var binding: FragmentOrderListBinding
    private val viewModel: OrderViewModel by viewModels()
    private lateinit var orderAdapter: OrderAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentOrderListBinding.bind(view)

        setupRecyclerView()
        setupListeners()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        orderAdapter = OrderAdapter { order ->
            // Detaya Git (Bunu sonra ekleyeceğiz)
            // context yerine requireContext() kullandık (Null safety için)
            Toast.makeText(requireContext(), "Detay ID: ${order.id}", Toast.LENGTH_SHORT).show()
        }
        binding.rvOrders.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = orderAdapter
        }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { findNavController().popBackStack() }

        // Chip Filtreleme
        binding.chipGroupFilter.setOnCheckedStateChangeListener { group, checkedIds ->
            val filter = when (checkedIds.firstOrNull()) {
                R.id.chipAll -> TimeFilter.ALL
                R.id.chipMonth -> TimeFilter.LAST_MONTH
                R.id.chip3Months -> TimeFilter.LAST_3_MONTHS
                R.id.chipYear -> TimeFilter.LAST_YEAR
                else -> TimeFilter.ALL
            }
            viewModel.filterOrders(filter)
        }
    }

    private fun observeViewModel() {
        viewModel.ordersState.observe(viewLifecycleOwner) { state ->
            // Artık OrderListState import edildiği için 'when' bloğu hata vermez
            when(state) {
                is OrderListState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.tvEmpty.visibility = View.GONE
                    binding.rvOrders.visibility = View.GONE
                }
                is OrderListState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.tvEmpty.visibility = View.GONE
                    binding.rvOrders.visibility = View.VISIBLE
                    // state.data artık tanınır
                    orderAdapter.submitList(state.data)
                }
                is OrderListState.Empty -> {
                    binding.progressBar.visibility = View.GONE
                    binding.rvOrders.visibility = View.GONE
                    binding.tvEmpty.visibility = View.VISIBLE
                }
                is OrderListState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}