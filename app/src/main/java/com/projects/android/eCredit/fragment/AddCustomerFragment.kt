package com.projects.android.eCredit.fragment


import android.Manifest
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.projects.android.eCredit.*
import com.projects.android.eCredit.database.Customer
import com.projects.android.eCredit.database.CustomerDatabase
import com.projects.android.eCredit.databinding.FragmentAddCustomerBinding
import kotlinx.android.synthetic.main.fragment_add_customer.*
import kotlinx.android.synthetic.main.layout_photo_options.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

/**
 * A simple [Fragment] subclass.
 */
class AddCustomerFragment : BaseFragment() {

    private var personName: String? = null
    private var contactImage: Bitmap? = null
    private var contactImageString: String? = null
    private var number: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = DataBindingUtil.inflate<FragmentAddCustomerBinding>(
            inflater,
            R.layout.fragment_add_customer,
            container,
            false
        )

        binding.tvConnectContact.setOnClickListener {

            // Setting up permissions to call the number
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_CONTACTS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
                startActivityForResult(intent, PICK_CONTACT)
            } else {
                requestContactsPermission()
            }


        }

        binding.ivEditPhoto.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            val inflater2 = requireActivity().layoutInflater
            val view = inflater2.inflate(R.layout.layout_photo_options, null)

            builder.setView(view)
                .setTitle("Add Photo")
                .setNegativeButton("cancel")
                    { dialog, _ -> dialog.cancel() }

            val dialog = builder.create()
            dialog.show()

            view.tvTakePhoto.setOnClickListener {

                // Setting up permission to Take Photo
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED) {
                    val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(takePictureIntent, OPEN_CAMERA)
                    dialog.dismiss()
                } else {
                    requestCameraPermission()
                    dialog.dismiss()
                }

            }

            view.tvChooseFromGallery.setOnClickListener {
                val pickPhotoIntent = Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )
                startActivityForResult(pickPhotoIntent, OPEN_GALLERY)
                dialog.dismiss()
            }

        }


        binding.btnSave.setOnClickListener {
            saveData()
            it.findNavController()
                .navigate(AddCustomerFragmentDirections.actionAddCustomerFragmentToNavHome())
        }

        setHasOptionsMenu(true)

        return binding.root
    }

    private fun saveData() {
        // This will take the person name into this variable
        personName = etName.text.toString()

        if (personName.toString().isEmpty()) {
            etName.error = "Name Required"
            etName.requestFocus()
//            return saveData()
        }

        if (etPhone.text.toString().isEmpty()) {
            etPhone.error = "Phone Required"
            etPhone.requestFocus()
//            return saveData()
        }

        if (etName.text.toString().isNotEmpty() && etPhone.text.toString().isNotEmpty()) {
            launch {
                val customer =
                    Customer(personName.toString(), etPhone.text.toString(), contactImageString)
                addNewCustomer(customer)
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_CONTACT) {
            if (resultCode == RESULT_OK) {
                val contactData = data?.data

                val cursor =
                    (context)?.contentResolver?.query(contactData!!, null, null, null, null)
                if (cursor!!.moveToFirst()) {

                    // Get Person Name From Contact List
                    personName =
                        cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME))

                    // Get Contact's Photo
                    val photoUri =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Photo.PHOTO_URI))
                    if (photoUri != null) {
                        if (android.os.Build.VERSION.SDK_INT >= 29) {
                            contactImage = ImageDecoder.decodeBitmap(
                                ImageDecoder.createSource(
                                    requireContext().contentResolver,
                                    Uri.parse(photoUri)
                                )
                            )
                        } else {
                            contactImage = MediaStore.Images.Media.getBitmap(
                                context?.contentResolver,
                                Uri.parse(photoUri)
                            )
                        }
                    }

                    if (contactImage != null) {
                        ivProfileImage.setImageBitmap(contactImage)
                    } else {
                        ivProfileImage.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.ic_panda
                            )
                        )
                    }

                    contactImageString = bitmapToString(contactImage)


                    // This Code Snippet Gets Person's Phone Number From Contact List
                    val id =
                        cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
                    val hasPhone =
                        cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER))

                    if (hasPhone.equals("1")) {
                        val numberCursor = (context)?.contentResolver?.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                            null,
                            null
                        )
                        numberCursor?.moveToFirst()
                        number = numberCursor?.getString(numberCursor.getColumnIndex("data1"))

                        etPhone.setText(number)
                        numberCursor?.close()
                    }

                    etName.setText(personName)
                    cursor.close()
                }

                contactImage = null
            }
        }

        if (requestCode == OPEN_CAMERA) {
            if (resultCode == RESULT_OK) {
                val selectedImage = data?.extras?.get("data") as Bitmap?

                val fOut = ByteArrayOutputStream()
                selectedImage?.compress((Bitmap.CompressFormat.PNG), 100, fOut)

                if (selectedImage != null) {
                    ivProfileImage.setImageBitmap(selectedImage)
                } else {
                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_LONG).show()
                }

                contactImageString = bitmapToString(selectedImage)

            }
        }

        if (requestCode == OPEN_GALLERY) {
            if (resultCode == RESULT_OK) {
                val selectedImageUri = data?.data

                if (android.os.Build.VERSION.SDK_INT >= 29) {
                    contactImage = ImageDecoder.decodeBitmap(
                        ImageDecoder.createSource(
                            requireContext().contentResolver,
                            selectedImageUri!!
                        )
                    )
                } else {
                    contactImage = MediaStore.Images.Media.getBitmap(
                        context?.contentResolver,
                        selectedImageUri
                    )
                }

//                val nh = contactImage!!.height * (512 / contactImage?.width!!)
//                val scaledImage = Bitmap.createScaledBitmap(contactImage!!, 512, nh, true)

//                val fOut = ByteArrayOutputStream()
//                contactImage?.compress((Bitmap.CompressFormat.PNG), 5, fOut)

//                val byteArray = fOut.toByteArray()
//                val encodedImage = Base64.getEncoder(byteArray, Base64)

                if (contactImage != null) {
                    ivProfileImage.setImageBitmap(contactImage)
                } else {
                    ivProfileImage.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_panda
                        )
                    )
                }

            }

            contactImage = null

        }
    }

    private suspend fun addNewCustomer(customer: Customer) {
        withContext(Dispatchers.IO) {
            CustomerDatabase.getInstance(activity!!).getCustomerDao.addCustomer(customer)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.save, menu)
    }

    private fun requestContactsPermission() {
        if (shouldShowRequestPermissionRationale(
                Manifest.permission.READ_CONTACTS
            )
        ) {
            requestPermissions(Array(1) { Manifest.permission.READ_CONTACTS }, READ_CONTACTS_CODE)

        } else {
            requestPermissions(Array(1) { Manifest.permission.READ_CONTACTS }, READ_CONTACTS_CODE)
        }
    }


    private fun requestCameraPermission() {
        if (shouldShowRequestPermissionRationale(
                Manifest.permission.CAMERA
            )
        ) {
            requestPermissions(Array(1) { Manifest.permission.CAMERA }, OPEN_CAMERA)

        } else {
            requestPermissions(Array(1) { Manifest.permission.CAMERA }, OPEN_CAMERA)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == READ_CONTACTS_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
                startActivityForResult(intent, PICK_CONTACT)
            } else {
                Toast.makeText(context, "Permission Denied!", Toast.LENGTH_SHORT).show()
            }
        }

        if (requestCode == OPEN_CAMERA) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(takePictureIntent, OPEN_CAMERA)
            } else {
                Toast.makeText(context, "Camera Permission Denied!", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_save -> {
                saveData()
                view?.findNavController()
                    ?.navigate(AddCustomerFragmentDirections.actionAddCustomerFragmentToNavHome())
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}

