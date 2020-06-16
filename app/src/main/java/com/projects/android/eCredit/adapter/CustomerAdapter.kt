package com.projects.android.eCredit.adapter

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.RecyclerView
import com.projects.android.eCredit.database.Customer
import com.projects.android.eCredit.R
import com.projects.android.eCredit.fragment.HomeFragmentDirections
import com.projects.android.eCredit.stringToBitmap
import kotlinx.android.synthetic.main.layout_customers.view.*

class CustomerAdapter(private val customers: List<Customer>) :
    RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder>() {

    private var imageFromString: Bitmap? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CustomerAdapter.CustomerViewHolder {
        return CustomerViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_customers, parent, false)
        )
    }

    override fun getItemCount(): Int = customers.size

    override fun onBindViewHolder(holder: CustomerAdapter.CustomerViewHolder, position: Int) {
        val customerId = customers[position].id
        val customerName = customers[position].name
        val customerPhoneNumber = customers[position].phoneNumber.toString()
        val imageString = customers[position].image

        holder.view.tvRecyclerName.text = customerName
        holder.view.tvRecyclerPhone.text = customerPhoneNumber
        imageFromString = stringToBitmap(customers[position].image)
        if (imageFromString != null) {
            holder.view.ivPersonImage.setImageBitmap(imageFromString)
        } else {
            holder.view.ivPersonImage.setImageResource(R.drawable.ic_panda)
        }

        holder.view.setOnClickListener {

            val extras = FragmentNavigatorExtras(
                holder.view.ivPersonImage to "ivPersonImage"
            )

            it.findNavController().navigate(
                HomeFragmentDirections.actionNavHomeToTransactionFragment(
                    customerId,
                    customerName,
                    customerPhoneNumber,
                    imageString
                ), extras
            )
        }
    }

    class CustomerViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}

