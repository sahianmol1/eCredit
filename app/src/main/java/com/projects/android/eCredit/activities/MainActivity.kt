package com.projects.android.eCredit.activities

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.projects.android.eCredit.R
import com.projects.android.eCredit.STORAGE_PERMISSION_CODE
import com.projects.android.eCredit.databinding.ActivityMainBinding
import com.projects.android.eCredit.viewModel.MainActivityViewModel

class MainActivity : BaseActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var mViewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityMainBinding>(
            this,
            R.layout.activity_main
        )
        mViewModel = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.nav_export,
                R.id.nav_all_transactions,
                R.id.nav_share
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

//        navView.setNavigationItemSelectedListener {
//
//            val newFragment: Fragment
//            val transaction = supportFragmentManager.beginTransaction()
//            when (it.itemId)  {
//                R.id.nav_share -> {
//                    val shareIntent = Intent()
//                    shareIntent.action = Intent.ACTION_SEND
//                    shareIntent.type = "text/plain"
//                    shareIntent.putExtra(Intent.EXTRA_TEXT, "Download App Now...")
//                    startActivity(shareIntent)
//                }
//
//                R.id.nav_send -> {
//                    val shareIntent = Intent()
//                    shareIntent.action = Intent.ACTION_SEND
//                    shareIntent.type = "text/plain"
//                    shareIntent.putExtra(Intent.EXTRA_TEXT, "Download App Now...")
//                    startActivity(shareIntent)
//                }
//
//                R.id.nav_all_transactions -> {
//                    newFragment = AllTransactionsFragment()
//                    transaction.replace(R.id.drawer_layout, newFragment)
//                    transaction.addToBackStack(null)
//                    transaction.commit()
//                }
//
//            }
//             return@setNavigationItemSelectedListener true
//        }

        // Requesting Storage Permission
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
        } else {
            requestStoragePermission()
        }

//        drawerLayout.setOnClickListener {
//            if (posi)
//        }


        setOnClickListeners()
    }

    private fun setOnClickListeners() {
//        extFab.setOnClickListener {
////            it.findNavController().navigate(R.id.action_nav_home_to_addCustomerFragment)
//        }
//        fun setExtFabClickListener(view: View) {
//            view.findNavController().navigate(R.id.action_nav_home_to_addCustomerFragment)
//        }
    }

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.main, menu)
//        return true
//    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        ) {

        } else {
            ActivityCompat.requestPermissions(
                this,
                Array<String>(1){(Manifest.permission.READ_EXTERNAL_STORAGE)} ,
                STORAGE_PERMISSION_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

//    override fun draw(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.nav_share -> {
//                Toast.makeText(this, "Share Intent Calling Here", Toast.LENGTH_LONG).show()
//            }
//        }
//        return super.onOptionsItemSelected(item)
//    }

}
