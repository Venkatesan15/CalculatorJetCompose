package com.example.calculatorjc

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.ViewCompat
import kotlin.math.absoluteValue

class MainActivity : AppCompatActivity() {


//    private val fragmentTwo = FragmentTwo()
    private val fragmentOne = FragmentOne()
    companion object {

        var containerOne by mutableStateOf(ViewCompat.generateViewId())
        var containerTwo by mutableStateOf(ViewCompat.generateViewId())

        const val portrait = "Portrait"
        const val landscape = "Landscape"
        const val fragmentOneTag = "FragmentOne"
        const val fragTwoArg = "fragTwoArg"
        const val resultAvailable = "resultAvail"
    }

    private var configuration by mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            Container(savedInstanceState)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        configuration = if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            landscape
        } else portrait
    }

    @Composable
    fun Container(savedInstanceState: Bundle?) {

            when (configuration) {

                portrait -> {
                    SetFragmentOne(Modifier.fillMaxSize(), savedInstanceState)
                }
                landscape -> {

                    Row {

                        SetFragmentOne(
                            Modifier
                                .weight(1f)
                                .fillMaxSize(), savedInstanceState
                        )


                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
                        ) {

                            AndroidView(factory = { context ->
                                FrameLayout(context).apply {
                                    id = containerTwo
                                }
                            },
                            update = {
                                if(savedInstanceState?.getBundle(fragTwoArg) != null) {

                                    val fragmentTwo = FragmentTwo()
                                    fragmentTwo.arguments = savedInstanceState.getBundle(fragTwoArg)
                                    supportFragmentManager.beginTransaction().replace(it.id, fragmentTwo, FragmentOne.frgBTag).commit()
                                }
                            })
                        }
                    }
                }

                else -> {

                    val currentConfiguration = LocalConfiguration.current
                    configuration = if (currentConfiguration.orientation == Configuration.ORIENTATION_LANDSCAPE) landscape else portrait
                }
            }

    }


    @Composable
    fun SetFragmentOne(modifier: Modifier, savedInstanceState: Bundle?) {

        val containerOneR by rememberSaveable { mutableStateOf(View.generateViewId()) }
        val containerTwoR by rememberSaveable { mutableStateOf(View.generateViewId()) }

        containerOne = containerOneR.absoluteValue
        containerTwo = containerTwoR.absoluteValue

        Box(modifier = modifier) {

            AndroidView( factory = { context ->

                FrameLayout(context).apply {
                    id = containerOne
                }
            },
                update = {
                    if(savedInstanceState?.getBundle(fragTwoArg) != null) {

                        val fragmentTwo = FragmentTwo()

                        fragmentTwo.arguments = savedInstanceState.getBundle(fragTwoArg)

                        if(configuration == portrait) {

                            val landContainerTwoFrg = supportFragmentManager.findFragmentById(containerTwoR)

                            val containerOneFrg = supportFragmentManager.findFragmentById(containerOneR)

                            if(containerOneFrg != null) {
                                supportFragmentManager.beginTransaction().remove(containerOneFrg).commit()
                            }
                            if(landContainerTwoFrg != null) {
                                supportFragmentManager.beginTransaction().remove(landContainerTwoFrg).commit()
                            }

                            val frgBForRemove = supportFragmentManager.findFragmentByTag(FragmentOne.frgBTag)
                            if(frgBForRemove != null) supportFragmentManager.beginTransaction().remove(frgBForRemove).commit()

                            supportFragmentManager.beginTransaction().apply {
                                replace(it.id, FragmentOne(), fragmentOneTag).commit()
                            }

                            supportFragmentManager.beginTransaction().apply {
                                addToBackStack(fragmentOneTag)
                                replace(it.id, fragmentTwo, FragmentOne.frgBTag).commit()
                            }
                        } else {
                            supportFragmentManager.popBackStack()
                            supportFragmentManager.beginTransaction().replace( it.id, fragmentOne, fragmentOneTag ).commit()
                        }

                    } else if(savedInstanceState?.getBundle(resultAvailable) != null) {
                        FragmentOne.actionPage = false
                        val bun = savedInstanceState.getBundle(resultAvailable)

                        FragmentOne.result  = bun?.getString(FragmentOne.resultText).toString()
                    }
                    else {
                        val fragmentA = supportFragmentManager.findFragmentByTag(fragmentOneTag)

                        if (fragmentA != null) {
                            supportFragmentManager.beginTransaction().remove(fragmentOne).commit()
                        }

                        supportFragmentManager.beginTransaction()
                            .replace(it.id, FragmentOne(), fragmentOneTag).commit()
                    }

                })

        }
    }

    override fun onSaveInstanceState(outState: Bundle) {

        val currentFragment = supportFragmentManager.findFragmentById(containerOne)
        if(configuration == portrait && currentFragment is FragmentTwo) {

            outState.putBundle( fragTwoArg, currentFragment.arguments)

        } else if (configuration == landscape){

            val landContainerTwo = supportFragmentManager.findFragmentById(containerTwo)

            if(landContainerTwo != null) {
                outState.putBundle(fragTwoArg, landContainerTwo.arguments)
            }
        }

        if(fragmentOne.arguments != null) {
            outState.putBundle(resultAvailable, fragmentOne.arguments?.getBundle(FragmentOne.resultText))
        }

        super.onSaveInstanceState(outState)

    }

    override fun onBackPressed() {
        val frgB = supportFragmentManager.findFragmentByTag(FragmentOne.frgBTag)

        FragmentTwo.inputOne = ""
        FragmentTwo.inputTwo = ""
        if(configuration == landscape && frgB != null) {
            supportFragmentManager.beginTransaction().remove(frgB).commit()
        }
        else super.onBackPressed()
    }

}