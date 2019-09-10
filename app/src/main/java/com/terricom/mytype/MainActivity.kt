package com.terricom.mytype

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.terricom.mytype.data.UserManager
import com.terricom.mytype.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : BaseActivity(){

    val viewModel : MainViewModel by lazy {
        ViewModelProviders.of(this).get(MainViewModel::class.java)
    }

    private lateinit var binding: ActivityMainBinding
    var isFABOpen: Boolean = false
    val time = Calendar.getInstance().time

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_food_record -> {
                if (UserManager.userToken != ""){
                    findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToDiaryFragment())
                }
                else findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToLoginFragment())

                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_diary -> {
                findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToLinechartFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_line_chart -> {
                findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToAchivementFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_harvest -> {
                findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToProfileFragment())
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    @SuppressLint("ObsoleteSdkInt")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        val navView: BottomNavigationView = findViewById(R.id.bottom_nav_view)

        val fab = findViewById<View>(R.id.fab) as FloatingActionButton

        fab.setOnClickListener {
            if (!isFABOpen) {
                showFABMenu()
            } else {
                closeFABMenu()
            }
        }
        binding.fab1.setOnClickListener {
            findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToFoodieFragment())
        }

        binding.fab2.setOnClickListener {
            findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToShapeRecordFragment())
        }
        binding.fab3.setOnClickListener {
            findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToSleepFragment())
        }
        binding.fabLayout3.setOnClickListener {
            findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToSleepFragment())
        }

        binding.fabLayout1.setOnClickListener {
            findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToFoodieFragment())
        }
        binding.fabLayout2.setOnClickListener {
            findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToShapeRecordFragment())
        }


        setupNavController()

        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)


    }


    private fun setupNavController() {
        findNavController(R.id.myNavHostFragment).addOnDestinationChangedListener { navController: NavController, _: NavDestination, _: Bundle? ->
            viewModel.currentFragmentType.value = when (navController.currentDestination?.id) {
                R.id.foodieFragment -> CurrentFragmentType.FOODIE
                R.id.diaryFragment -> CurrentFragmentType.DIARY
                R.id.linechartFragment -> CurrentFragmentType.LINECHART
                R.id.achivementFragment -> CurrentFragmentType.HARVEST
                R.id.loginFragment -> CurrentFragmentType.LOGIN
                R.id.shaperecordFragment -> CurrentFragmentType.SHAPE_RECORD
                R.id.referenceDialog -> CurrentFragmentType.REF
                R.id.profileFragment -> CurrentFragmentType.PROFILE
                R.id.dreamboardFragment -> CurrentFragmentType.DREAMBOARD
                R.id.sleepFragment -> CurrentFragmentType.SLEEP
                else -> viewModel.currentFragmentType.value
            }
        }
        viewModel.currentFragmentType.observe(this, Observer {
            Log.i("Terri", "viewModel.currentFragmentType.observe = ${it.value}")
//            binding.textToolbarTitle.text = it.value
            if (it.value == ""){
                hideBottomNavView()
                hideToolbar()
                hideFABView()
            }
            if (it.value == App.instance?.getString(R.string.title_foodie) || it.value == App.instance?.getString(R.string.title_shape_record) || it.value == App.instance?.getString(R.string.title_sleep)  ){
                hideBottomNavView()
                hideFABView()
            }

        })
    }

    fun hideToolbar(){
//        binding.toolbar.visibility = View.GONE
    }

    fun hideBottomNavView(){
        binding.bottomNavView.visibility = View.GONE
    }

    fun hideFABView(){
        binding.fab.visibility = View.GONE
        binding.fabLayout1.visibility = View.GONE
        binding.fabLayout2.visibility = View.GONE
        binding.fabLayout3.visibility - View.GONE
        binding.fab1.visibility = View.GONE
        binding.fab2.visibility = View.GONE
        binding.fab3.visibility = View.GONE
    }


    private fun showFABMenu() {
        isFABOpen = true
        fabLayout1.animate().translationY(-resources.getDimension(R.dimen.standard_55))
        fabLayout2.animate().translationY(-resources.getDimension(R.dimen.standard_105))
        fabLayout3.animate().translationY(-resources.getDimension(R.dimen.standard_155))
        binding.fab1.visibility = View.VISIBLE
        binding.fab2.visibility = View.VISIBLE
        binding.fab3.visibility = View.VISIBLE
        binding.fabLayout1.visibility = View.VISIBLE
        binding.fabLayout2.visibility = View.VISIBLE
        binding.fabLayout3.visibility = View.VISIBLE


    }

    private fun closeFABMenu() {
        isFABOpen = false
        fabLayout1.animate().translationY(resources.getDimension(R.dimen.standard_0))
        fabLayout2.animate().translationY(resources.getDimension(R.dimen.standard_0))
        fabLayout3.animate().translationY(resources.getDimension(R.dimen.standard_0))

        Handler().postDelayed({
            binding.fabLayout1.visibility = View.INVISIBLE
            binding.fabLayout2.visibility = View.INVISIBLE
            binding.fabLayout3.visibility = View.INVISIBLE}, 300)
    }


}
