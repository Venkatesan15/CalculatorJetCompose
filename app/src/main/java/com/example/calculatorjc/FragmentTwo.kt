package com.example.calculatorjc

import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.text.DecimalFormat


class FragmentTwo : Fragment() {


    companion object {
        var btnText by mutableStateOf("")
        var inputOne by mutableStateOf("")
        var inputTwo by mutableStateOf("")

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = ComposeView(requireContext())

        view.apply {

            setContent {
                GetInput()
            }
        }
        return view
    }

    @Composable
    fun GetInput() {

        val focusManager = LocalFocusManager.current

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Gray),
            verticalArrangement = Arrangement.Center,
        ) {

            TextField(
                value = inputOne, onValueChange = {
                    if (!it.contains(',')) {
                        inputOne = it
                    }
                },
                modifier = Modifier.align(Alignment.CenterHorizontally),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number, imeAction = ImeAction.Next
                ),
                singleLine = true,
                label = { Text("Number One") }
            )

            Spacer(modifier = Modifier.size(30.dp))

            TextField(
                value = inputTwo,
                onValueChange = {
                    if (!it.contains(',')) {
                        inputTwo = it
                    }
                },
                modifier = Modifier.align(Alignment.CenterHorizontally),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() }),
                singleLine = true,
                label = { Text("Number Two") },

                )

            Spacer(modifier = Modifier.size(70.dp))


            Button(onClick = {
                onClick()
            }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                if (btnText.isEmpty()) {
                    Text(text = arguments?.getString(FragmentOne.action).toString())
                } else {
                    Text(text = btnText)
                }
            }
        }
    }

    private fun onClick() {

        if (inputOne.trim().isNotEmpty() && inputTwo.trim().isNotEmpty()) {

            val fragmentOne = parentFragmentManager.findFragmentByTag(MainActivity.fragmentOneTag)
            if (fragmentOne != null) {
                fragmentOne.arguments = getResultBundle(
                    inputOne,
                    inputTwo,
                    btnText
                )
            }

            if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                parentFragmentManager.popBackStack()
            } else {
                parentFragmentManager.beginTransaction().remove(this@FragmentTwo).commit()
            }


            inputOne = ""
            inputTwo = ""
            arguments = null

        } else {
            Toast.makeText(context, "Please Enter Input", Toast.LENGTH_SHORT).show()
        }
        FragmentOne.actionPage = false
    }

    private fun getResultBundle(input1: String, input2: String, action: String): Bundle {

        val num1 = input1.toFloat()
        val num2 = input2.toFloat()

        val ans = when (action) {
            "Add" -> (num1 + num2)
            "Subtract" -> (num1 - num2)
            "Multiply" -> (num1 * num2)
            "Division" -> (num1 / num2)
            else -> null!!
        }

        val format = DecimalFormat("0.#")

        val bundle = Bundle()

        val resultText =
            "Your Result is ${format.format(ans)} for inputs $input1 and $input2 with action $action"
        FragmentOne.result = resultText
        bundle.putString(FragmentOne.resultText, resultText)
        return bundle
    }
}

