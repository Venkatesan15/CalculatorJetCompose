package com.example.calculatorjc

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.fragment.app.Fragment


class FragmentOne : Fragment() {

    private val fragmentTwo = FragmentTwo()

    companion object {

        var result by mutableStateOf("")

        const val frgBTag = "FragmentB"
        const val resultText = "ResultText"
        const val action = "Action"
        var actionPage by mutableStateOf(true)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return ComposeView(requireContext()).apply {
            setContent {

                if(result.isNotEmpty() && !actionPage) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Cyan)
                    ) {
                        Text(text = result,
                            modifier = Modifier.fillMaxWidth(),
                            color = Color.Black,
                            textAlign = TextAlign.Center,
                            fontSize = 30.sp,

                            )
                        Spacer(modifier = Modifier.size(30.dp))
                        Button(onClick = { actionPage = true
                                         result = ""
                                         },Modifier.fillMaxWidth()) {
                            Text(text = "Reset")
                        }
                    }

                }
                else if(actionPage){

                    SetActions()
                }
            }
        }
    }


    @Composable
    fun SetActions() {

        var onClick by remember {
            mutableStateOf(false)
        }
        val bundle by remember {
            mutableStateOf(Bundle())
        }

        if(onClick) onClick = createFragmentTwo(bundle = bundle)

        ConstraintLayout(modifier = Modifier
            .fillMaxSize()
            .background(Color.Cyan)) {

            val (addBtn, subBtn, multiplyBtn, divBtn) = createRefs()

            Button(onClick = {
                bundle.putString(action, "Add")
                onClick = true


            }, modifier = Modifier.constrainAs(addBtn){

                width = Dimension.fillToConstraints
                linkTo(multiplyBtn.start, multiplyBtn.end, bias = 0.45f)
                linkTo(parent.top, parent.bottom, bias = 0.4f)


            } ) { Text(text = "Add") }



            Button(onClick = {
                bundle.putString(action, "Subtract")
                onClick = true
                }, modifier = Modifier.constrainAs(subBtn){


                linkTo(parent.top, parent.bottom, bias = 0.4f)
                linkTo(parent.start, parent.end, bias = 0.75f)
            }) { Text(text = "Subtract") }

            Button(onClick = {
                bundle.putString(action, "Multiply")
                onClick = true

                }, modifier = Modifier.constrainAs(multiplyBtn){

                linkTo(parent.top, parent.bottom, bias = 0.6f)
                linkTo(parent.start, parent.end, bias = 0.3f)

            }) { Text(text = "Multiply") }

            Button(onClick = {
                bundle.putString(action, "Division")
                onClick = true
                },
                modifier = Modifier.constrainAs(divBtn)  {

                width = Dimension.fillToConstraints
                linkTo(parent.top, parent.bottom, bias = 0.6f)
                linkTo(subBtn.start, subBtn.end, bias = 0.7f)
            }) {
                Text(text = "Division")
            }
        }

    }

    @Composable
    fun createFragmentTwo(bundle: Bundle) : Boolean {

        FragmentTwo.btnText = bundle.getString(action).toString()

//        var configuration by remember {
//            mutableStateOf("")
//        }

        //val currentConfiguration = LocalConfiguration.current

        //configuration = if (currentConfiguration.orientation == Configuration.ORIENTATION_LANDSCAPE) MainActivity.landscape else MainActivity.portrait

        fragmentTwo.arguments = bundle

        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {

            parentFragmentManager.beginTransaction().apply {
                addToBackStack(MainActivity.fragmentOneTag)

                replace(MainActivity.containerOne, fragmentTwo, frgBTag).commit()
            }
        }
        else {

            println("Landscape")
            val frgB = parentFragmentManager.findFragmentByTag(frgBTag)
            if(frgB != null) {
                FragmentTwo.btnText = bundle.getString(action).toString()
            }
            else {
                parentFragmentManager.beginTransaction().apply {
                    replace(MainActivity.containerTwo, fragmentTwo, frgBTag).commit()
                }
            }
        }
        return false
    }
}