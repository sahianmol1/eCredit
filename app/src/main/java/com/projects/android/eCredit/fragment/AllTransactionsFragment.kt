package com.projects.android.eCredit.fragment


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.projects.android.eCredit.R
import com.projects.android.eCredit.adapter.AllTransactionsAdapter
import com.projects.android.eCredit.database.CustomerDatabase
import com.projects.android.eCredit.database.Transaction
import kotlinx.android.synthetic.main.fragment_all_transactions.*
import kotlinx.android.synthetic.main.fragment_all_transactions.tvAmountDue
import kotlinx.android.synthetic.main.fragment_all_transactions.tvBalanceDue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * A simple [Fragment] subclass.
 */
class AllTransactionsFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_transactions, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        allTransactionsRecyclerView.layoutManager = LinearLayoutManager(activity)
        launch {
            context?.let {
                val allTransactions = getAllTransactions()
                allTransactionsRecyclerView.adapter = AllTransactionsAdapter(allTransactions, activity as Context)

                scrollview.post {
                    scrollview.fullScroll(View.FOCUS_DOWN)
                }
                val dueAmount = calculateDueAmount(allTransactions)


                if (dueAmount > 0) {
                    tvAmountDue.setTextColor(
                        ContextCompat.getColor(
                            requireActivity(),
                            R.color.holo_red_dark
                        )
                    )
                    tvAmountDue.text = "₹" + dueAmount.toString()
                    tvBalanceDue.text = getString(R.string.balance_due)
                    tvBalanceDue.setTextColor(
                        ContextCompat.getColor(
                            requireActivity(),
                            R.color.holo_red_dark
                        )
                    )
                } else {
                    tvAmountDue.setTextColor(
                        ContextCompat.getColor(
                            requireActivity(),
                            R.color.colorPrimary
                        )
                    )
                    tvAmountDue.text = "₹" + (dueAmount * -1).toString()
                    tvBalanceDue.text = getString(R.string.balance_available)
                    tvBalanceDue.setTextColor(
                        ContextCompat.getColor(
                            requireActivity(),
                            R.color.colorPrimary
                        )
                    )
                }

            }
        }
    }

    private suspend fun getAllTransactions(): List<Transaction> {
        return withContext(Dispatchers.IO) {
            val transactions = CustomerDatabase.getInstance(requireActivity())
                .getTransactionDao.getAllTransactionsForAllCustomers()
            transactions
        }

    }

    private fun calculateDueAmount(transactions: List<Transaction>): Int {

        var dueAmount: Int = 0
        for (transaction in transactions) {
            if (transaction.youGave == true) {
                dueAmount += transaction.amount.toInt()
            } else {
                dueAmount -= transaction.amount.toInt()
            }
        }
        return dueAmount
    }


}
