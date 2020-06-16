package com.projects.android.eCredit.fragment

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.projects.android.eCredit.database.Customer
import com.projects.android.eCredit.database.CustomerDatabase
import com.projects.android.eCredit.R
import com.projects.android.eCredit.adapter.CustomerAdapter
import com.projects.android.eCredit.databinding.FragmentHomeBinding
import com.projects.android.eCredit.viewModel.HomeViewModel
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class HomeFragment : BaseFragment() {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val mDataBinding = DataBindingUtil.inflate<FragmentHomeBinding>(
            inflater,
            R.layout.fragment_home,
            container,
            false
        )

//        val animation = AnimationUtils.loadAnimation(context, R.anim.forward_arrow_animation)
//        ivRightArrow.startAnimation(animation)

        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)

        // This will set up the layout for RecyclerView
        mDataBinding.recyclerView.layoutManager = LinearLayoutManager(requireActivity())

        // This will get all the customers from Database
        launch {
            context?.let {
                try {
                    val customers = getAllCustomers()
                    recyclerView.adapter =
                        CustomerAdapter(customers)
                    if (customers.isEmpty()) {
                        mDataBinding.tvInfoAddCustomer.visibility = View.VISIBLE
                        mDataBinding.ivRightArrow.visibility = View.VISIBLE
                    }


                } catch (e: Exception) {
                    Toast.makeText(context, "Something is wrong, take a backup, delete the app", Toast.LENGTH_LONG).show()

                }

            }
        }

        setHasOptionsMenu(true)

        return mDataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        extFab.setOnClickListener {
            it.findNavController()
                .navigate(HomeFragmentDirections.actionNavHomeToAddCustomerFragment())
        }
    }

    // This is a suspend function which will get all the birthdays from database
    private suspend fun getAllCustomers(): List<Customer> {
        return withContext(Dispatchers.IO) {
            val birthdays = CustomerDatabase.getInstance(requireActivity()).getCustomerDao.getCustomers()
            birthdays
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == R.id.action_new_customer) {
            view?.findNavController()?.navigate(HomeFragmentDirections.actionNavHomeToAddCustomerFragment())
            return true
        }

        return super.onOptionsItemSelected(item)

    }


}