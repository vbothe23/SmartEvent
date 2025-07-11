package com.vishalbothe.smartevent

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
@Preview
fun MainScreen(viewModel: MainViewModel = viewModel()) {
    val status by viewModel.status.collectAsState()

    var eventName by remember { mutableStateOf("") }
//    var eventProps by remember { mutableStateOf("") }
    var eventProps by remember { mutableStateOf<Map<String, Any>>(emptyMap()) }
    var clearKeyValueInputs by remember { mutableStateOf(0) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = eventName,
            onValueChange = { eventName = it },
            label = { Text("Event Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

//        OutlinedTextField(
//            value = eventProps,mutableStateListOf<Pair<String, String>>()
//            onValueChange = { eventProps = it },
//            label = { Text("Event Properties (JSON)") },
//            modifier = Modifier.fillMaxWidth()
//        )
        KeyValueInputList(clearKeyValueInputs) { props ->
            eventProps = props
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.logEvent(eventName, eventProps)
                eventName = ""
                eventProps = emptyMap()
                clearKeyValueInputs++
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Log Events") }

        Button(
            onClick = {viewModel.flushEvents()},
            modifier = Modifier.fillMaxWidth()
        ) { Text("Flush Events") }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Status:\n$status",
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme
                .typography.bodyMedium
        )
    }
}

@Composable
fun KeyValueInputList(
    clearTrigger: Int,
    onSubmit: (Map<String, Any>) -> Unit
) {
    var key by remember { mutableStateOf("") }
    var value by remember { mutableStateOf("") }

    val keyValueList = remember { mutableStateListOf<Pair<String, String>>() }

    LaunchedEffect(clearTrigger) {
        key = ""
        value = ""
        keyValueList.clear()
        onSubmit(emptyMap())
    }

    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = key,
                onValueChange = { key = it },
                label = { Text("Key") },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                value = value,
                onValueChange = { value = it },
                label = { Text("Value") },
                modifier = Modifier.weight(1f)
            )
            Button(onClick = {
                if (key.isNotBlank() && value.isNotBlank()) {
                    keyValueList.add(key to value)
                    key = ""
                    value = ""
                }
            }) {
                Text("+")
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        if (keyValueList.isNotEmpty()) {
            Text("Added Properties:")
            keyValueList.forEach {
                Text("- ${it.first}: ${it.second}")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                val map = keyValueList.associate { it.first to it.second }
                onSubmit(map)
            },
            enabled = keyValueList.isNotEmpty(),
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Submit Properties")
        }
    }
}