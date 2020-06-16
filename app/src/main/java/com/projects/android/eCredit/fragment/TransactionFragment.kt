package com.projects.android.eCredit.fragment


import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionInflater
import com.deishelon.roundedbottomsheet.RoundedBottomSheetDialog
import com.projects.android.eCredit.CALL_PHONE_CODE
import com.projects.android.eCredit.database.CustomerDatabase
import com.projects.android.eCredit.database.Transaction
import com.projects.android.eCredit.R
import com.projects.android.eCredit.activities.MainActivity
import com.projects.android.eCredit.adapter.TransactionAdapter
import com.projects.android.eCredit.stringToBitmap
import kotlinx.android.synthetic.main.fragment_transaction.*
import kotlinx.android.synthetic.main.layout_dialog_transaction.view.*
import kotlinx.android.synthetic.main.layout_transaction_bottom_sheet.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class TransactionFragment : BaseFragment() {
    private lateinit var mainActivity: MainActivity
    private lateinit var args: TransactionFragmentArgs
    private lateinit var mBottomSheetDialog: RoundedBottomSheetDialog
    private lateinit var transactions: List<Transaction>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_transaction, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition =
            TransitionInflater.from(context).inflateTransition(android.R.transition.move)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mBottomSheetDialog = RoundedBottomSheetDialog(requireContext())
        val sheetView = layoutInflater.inflate(R.layout.layout_transaction_bottom_sheet, null)
        mBottomSheetDialog.setContentView(sheetView)
        sheetView.btnBottomSheetCancel.setOnClickListener {
            mBottomSheetDialog.hide()
        }

        sheetView.tvBottomSheetMessage.setOnClickListener {
            val smsIntent = Intent(Intent.ACTION_SENDTO)
            smsIntent.addCategory(Intent.CATEGORY_DEFAULT)
            smsIntent.data = Uri.parse("sms:" + args.phoneNumber)
            startActivity(smsIntent)
        }

        sheetView.tvBottomSheetWhatsApp.setOnClickListener {
            try {
                val uri = Uri.parse("whatsapp://send?phone=+91" + args.phoneNumber)
                val sendIntent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(sendIntent)
            } catch (e: Exception) {
                e.printStackTrace()
                val appPackageName = "com.whatsapp"
                try {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
                } catch (e :android.content.ActivityNotFoundException) {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
                }
            }

        }

        args = TransactionFragmentArgs.fromBundle(requireArguments())
        val imageString = args.image
        val image = stringToBitmap(imageString)
        if (image != null) {
            ivPersonImage.setImageBitmap(image)
        }
        setHasOptionsMenu(true)

        mainActivity = this.activity as MainActivity

        setOnClickListeners()

    }

    override fun onResume() {
        super.onResume()
        mainActivity.supportActionBar?.title = args.name
        recyclerViewTrnsaction.layoutManager = LinearLayoutManager(activity)

        launch {
            context?.let {
                transactions = getAllTransactions(args.id)
                val transactionAdapter = TransactionAdapter(transactions)
                recyclerViewTrnsaction.adapter = transactionAdapter

                nestedScrollView.post {
                    nestedScrollView.fullScroll(View.FOCUS_DOWN)
                }

                val dueAmount = calculateDueAmount(transactions)

                if (dueAmount >= 0) {
                    tvAmountDue.setTextColor(
                        ContextCompat.getColor(
                            requireActivity(),
                            R.color.holo_red_dark
                        )
                    )
                    tvAmountDue.text = "₹" + dueAmount.toString()
                    tvBalanceDue.text = args.name.trim().split(" ")[0] + " owes you"
                    tvBalanceDue.setTextColor(
                        ContextCompat.getColor(
                            requireActivity(),
                            R.color.holo_red_dark
                        )
                    )
                }
                else {
                    tvAmountDue.setTextColor(
                        ContextCompat.getColor(
                            requireActivity(),
                            R.color.colorPrimary
                        )
                    )
                    tvAmountDue.text = "₹" + (dueAmount * -1).toString()
                    tvBalanceDue.text = "You Owe " + args.name.trim().split(" ")[0]
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.delete, menu)
    }

    private fun setOnClickListeners() {
        btnYouGot.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.layout_dialog_transaction, null)

            builder.setView(view)
                .setTitle("Enter Amount and Notes")
                .setNegativeButton(
                    "cancel",
                    DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })
                .setPositiveButton("save", DialogInterface.OnClickListener { dialog, which ->
                    val amount: String? = view.textFieldEnterAmount.text.toString()
                    val notes: String? = view.textFieldNotes.text.toString()
                    val currentDate = Calendar.getInstance().time
                    val currentTime = currentDate.time

                    if (amount == "") {
                        Toast.makeText(
                            context,
                            "Amount can't be empty. Please try again",
                            Toast.LENGTH_LONG
                        ).show()

                    } else {
                        launch {
                            val transaction =
                                Transaction(
                                    args.id,
                                    amount!!,
                                    currentDate,
                                    notes!!,
                                    currentTime,
                                    false
                                )
                            saveTransaction(transaction)
                            onResume()
                        }
                    }
                })
                .show()
        }

        btnYouGave.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.layout_dialog_transaction, null)

            builder.setView(view)
                .setTitle("Enter Amount and Notes")
                .setNegativeButton(
                    "cancel",
                    DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })
                .setPositiveButton("save", DialogInterface.OnClickListener { dialog, which ->
                    val amount: String? = view.textFieldEnterAmount.text.toString()
                    val notes: String? = view.textFieldNotes.text.toString()

                    val currentDate = Calendar.getInstance().time
                    val currentTime = currentDate.time

                    if (amount == "") {
                        Toast.makeText(
                            context,
                            "Amount can't be empty. Please try again",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        launch {
                            val transaction =
                                Transaction(
                                    args.id,
                                    amount!!,
                                    currentDate,
                                    notes!!,
                                    currentTime,
                                    true
                                )
                            saveTransaction(transaction)
                            onResume()
                        }
                    }
                })
                .show()
        }

        btnMessage.setOnClickListener {
            mBottomSheetDialog.show()
        }

        btnCall.setOnClickListener {

            // Requesting the Permissions
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CALL_PHONE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val intent =
                    Intent(Intent.ACTION_CALL, Uri.parse("tel:" + args.phoneNumber))
                startActivity(intent)
            } else {
                requestPhoneCallPermission()
            }


        }

    }

    private suspend fun saveTransaction(transaction: Transaction) {
        withContext(Dispatchers.IO) {
            CustomerDatabase.getInstance(activity!!).getTransactionDao.addTransaction(transaction)
        }
    }

    private suspend fun getAllTransactions(id: Int): List<Transaction> {
        return withContext(Dispatchers.IO) {
            val transactions =
                CustomerDatabase.getInstance(activity!!).getTransactionDao.getAllTransactions(id)
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

    private fun requestPhoneCallPermission() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE)) {
            requestPermissions(Array(1) { Manifest.permission.CALL_PHONE }, CALL_PHONE_CODE)
        } else {
            requestPermissions(Array(1) { Manifest.permission.CALL_PHONE }, CALL_PHONE_CODE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == CALL_PHONE_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intent =
                    Intent(Intent.ACTION_CALL, Uri.parse("tel:" + args.phoneNumber))
                startActivity(intent)
            } else {
                Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_delete) {
            val builder = AlertDialog.Builder(context)
                .setTitle("Are You Sure?")
                .setMessage("The Customer and all of his transactions will be deleted permanently")
                .setNegativeButton("cancel") { dialog, _ ->
                    dialog.cancel()
                }
                .setPositiveButton("Delete") { _, _ ->
                    launch {
                        context?.let {
                            deleteCustomer(args.id)
                            view?.findNavController()
                                ?.navigate(TransactionFragmentDirections.actionTransactionFragmentToNavHome())
                        }
                    }
                }
                .show()
        }
        return super.onOptionsItemSelected(item)
    }

    private suspend fun deleteCustomer(id: Int) {
        withContext(Dispatchers.IO) {
            CustomerDatabase.getInstance(requireActivity()).getCustomerDao.deleteCustomer(id)
        }
    }
}
