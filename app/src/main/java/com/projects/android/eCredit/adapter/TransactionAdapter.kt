package com.projects.android.eCredit.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.projects.android.eCredit.database.Transaction
import com.projects.android.eCredit.R
import kotlinx.android.synthetic.main.layout_transactions.view.*
import java.text.SimpleDateFormat
import java.util.*

class TransactionAdapter(private val transactions: List<Transaction>) :
    RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    private var dueAmount: Int = 0

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TransactionViewHolder {
        return TransactionViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_transactions, parent, false)
        )
    }

    override fun getItemCount(): Int = transactions.size

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        
        val time = transactions[position].time
        val timeFormatter = SimpleDateFormat("HH:mm", Locale.US)
        val formattedTimeString = timeFormatter.format(time)

        val date = transactions[position].date
        val dateFormatter = SimpleDateFormat("dd MMM, yyyy", Locale.US)
        val formattedDateString = dateFormatter.format(date)

        if ((position > 0) && (dateFormatter.format(transactions[position].date) == dateFormatter.format(
                transactions[position - 1].date
            ))
        ) {
            holder.view.tvDateGrouped.visibility = View.GONE
        } else {
            holder.view.tvDateGrouped.visibility = View.VISIBLE
            holder.view.tvDateGrouped.text = formattedDateString
        }

        if (transactions[position].youGave) {
            dueAmount += transactions[position].amount.toInt()
            holder.view.tvAmountRed.text = "₹" + transactions[position].amount
            holder.view.tvDateRed.text = formattedDateString
            holder.view.tvTimeRed.text = formattedTimeString
            if (transactions[position].notes != "") {
                holder.view.tvNotesRed.text = transactions[position].notes
            } else {
                holder.view.tvNotesRed.visibility = View.GONE
            }

            holder.view.tvTimeGreen.visibility = View.GONE
            holder.view.tvDateGreen.visibility = View.GONE
            holder.view.tvAmountGreen.visibility = View.GONE
            holder.view.constraintViewGreen.visibility = View.GONE
        } else {
            dueAmount -= transactions[position].amount.toInt()
            holder.view.tvAmountGreen.text = "₹" + transactions[position].amount
            holder.view.tvDateGreen.text = formattedDateString
            holder.view.tvTimeGreen.text = formattedTimeString
            if (transactions[position].notes != "") {
                holder.view.tvNotesGreen.text = transactions[position].notes
            } else {
                holder.view.tvNotesGreen.visibility = View.GONE
            }

            holder.view.tvTimeRed.visibility = View.GONE
            holder.view.tvDateRed.visibility = View.GONE
            holder.view.tvAmountRed.visibility = View.GONE
            holder.view.constraintViewRed.visibility = View.GONE
        }
    }


    class TransactionViewHolder(val view: View) : RecyclerView.ViewHolder(view)

}