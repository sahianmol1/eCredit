package com.projects.android.eCredit.fragment


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import com.projects.android.eCredit.database.Customer
import com.projects.android.eCredit.database.CustomerDatabase
import com.projects.android.eCredit.database.Transaction
import com.projects.android.eCredit.R
import com.projects.android.eCredit.generateFile
import com.projects.android.eCredit.goToFileIntent
import kotlinx.android.synthetic.main.fragment_export_csv.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

/**
 * A simple [Fragment] subclass.
 */
class ExportCSVFragment : BaseFragment() {

    private lateinit var customerList: List<Customer>
    private lateinit var transactionList: List<Transaction>
    var customerTransactionList = ArrayList<ArrayList<String>>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_export_csv, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        btnExportCSV.setOnClickListener {
            exportDatabaseToCSVFile()
        }

        customerTransactionList.add(arrayListOf("id", "Name", "PhoneNumber", "Amount", "Notes", "YouGave"))

        launch {
            context?.let {
                customerList = getAllCustomers()
                for (customer in customerList) {
                    transactionList = getAllTransactions(customer.id)
                    Log.i("TRANSACTION LIST SIZE", transactionList.size.toString())
                    for(transaction in transactionList) {
                        Log.i("ExportCSVFragment", "Fragment Created")
                        val everyThingList = arrayListOf<String>(customer.id.toString(), customer.name, customer.phoneNumber!!, transaction.amount, transaction.notes,
                            transaction.youGave.toString())

                        customerTransactionList.add(everyThingList)
                    }
                }
//                transactionList = getAllTransactionsForAllCustomers()
            }
        }


    }

    private fun getCSVFileName(): String = "UdhaarKhataDatabase.csv"

    private fun exportDatabaseToCSVFile() {
        val csvFile = generateFile(requireContext(), getCSVFileName())
        if (csvFile != null) {
//            if (MOVIES_SHOWN) {
//                (shownFragment as MoviesListFragment).exportMoviesWithDirectorsToCSVFile(csvFile)
//            } else {
//                (shownFragment as DirectorsListFragment).exportDirectorsToCSVFile(csvFile)
//            }

            exportMoviesWithDirectorsToCSVFile(csvFile)

            val intent = goToFileIntent(requireContext(), csvFile)
            try {
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Unable to find a suitable App to open the CSV file",
                    Toast.LENGTH_LONG
                ).show()
            }
        } else {
            Toast.makeText(context, "Unable to generate the CSV file", Toast.LENGTH_LONG).show()
        }
    }

    // This is a suspend function which will get all the customers from database
    private suspend fun getAllCustomers(): List<Customer> {
        return withContext(Dispatchers.IO) {
            val customers = CustomerDatabase.getInstance(activity!!).getCustomerDao.getCustomers()
            customers
        }
    }

    private suspend fun getAllTransactionsForAllCustomers(): List<Transaction> {
        return withContext(Dispatchers.IO) {
            val transactions = CustomerDatabase.getInstance(activity!!)
                .getTransactionDao.getAllTransactionsForAllCustomers()
            transactions
        }
    }

    private suspend fun getAllTransactions(id: Int): List<Transaction> {
        return withContext(Dispatchers.IO) {
            val transactions =
                CustomerDatabase.getInstance(activity!!).getTransactionDao.getAllTransactions(id)
            transactions
        }
    }

    fun exportMoviesWithDirectorsToCSVFile(csvFile: File) {

        csvWriter().open(csvFile, append = false) {
            // Header
//            writeRow(listOf("[id]", "[Customer]", "[Transaction]"))

//            writeRow(
//                listOf(
//                    "id",
//                    "Customer Name",
//                    "Customer Name",
//                    "Transaction Amount",
//                    "You Gave"
//                )
//            )

            writeRows(customerTransactionList)
//            writeRow(listOf("id", "Customer Name", "Customer Name","Transaction Amount", "You Gave"))


//            for (customer in customerList) {
//                val customerName = customer.name
//                val customerPhone = customer.phoneNumber
//                val customerId = customer.id
//
//                for (transaction in transactionList) {
//
//                    Log.i("ExportCSVFragment", "Fragment Created")
//
//                    if (transaction.id == customerId) {
//                        val transactionAmount = transaction.amount
//                        val youGave = transaction.youGave
//                        val transactionCustomerId = transaction.id
//
//                        writeRow(
//                            listOf(
//                                customerId,
//                                customerName,
//                                customerPhone,
//                                transactionCustomerId,
//                                transactionAmount,
//                                youGave
//                            )
//                        )
//                    }
//                }
//            }
        }
    }


}
