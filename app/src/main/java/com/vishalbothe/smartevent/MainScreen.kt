package com.vishalbothe.smartevent

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
@Preview
fun MainScreen(viewModel: MainViewModel = viewModel()) {
    val status by viewModel.status.collectAsState()

    var eventName by remember { mutableStateOf("") }
    var eventProps by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = eventName,
            onValueChange = { eventName = it },
            label = { Text("Event Name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = eventProps,
            onValueChange = { eventProps = it },
            label = { Text("Event Properties (JSON)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {viewModel.logEvent(eventName, eventProps)},
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