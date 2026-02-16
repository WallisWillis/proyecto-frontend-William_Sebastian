package com.example.wjuegosapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppNavigation()
        }
    }
}

@Composable
fun AppNavigation() {

    var currentScreen by remember { mutableIntStateOf(0) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when (currentScreen) {
            0 -> MenuScreen(
                onNavigateToLoteria = { currentScreen = 1 },
                onNavigateToAdivina = { currentScreen = 2 },
                onNavigateToParImpar = { currentScreen = 3 },
                onNavigateToBlackjack = { currentScreen = 4 }
            )
            1 -> LoteriaScreen(onBack = { currentScreen = 0 })
            2 -> AdivinaScreen(onBack = { currentScreen = 0 })
            3 -> ParImparScreen(onBack = { currentScreen = 0 })
            4 -> BlackjackScreen(onBack = { currentScreen = 0 })
        }
    }
}

@Composable
fun MenuScreen(
    onNavigateToLoteria: () -> Unit,
    onNavigateToAdivina: () -> Unit,
    onNavigateToParImpar: () -> Unit,
    onNavigateToBlackjack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "MENÚ DE JUEGOS", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = onNavigateToLoteria, modifier = Modifier.fillMaxWidth()) {
            Text("Jugar Lotería")
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onNavigateToAdivina, modifier = Modifier.fillMaxWidth()) {
            Text("Adivina el Número")
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onNavigateToParImpar, modifier = Modifier.fillMaxWidth()) {
            Text("Par o Impar")
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onNavigateToBlackjack,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20))
        ) {
            Text("Blackjack (21)")
        }
    }
}

@Composable
fun LoteriaScreen(onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    var numeros by remember { mutableStateOf("Presiona Jugar") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("LOTERÍA", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(24.dp))
        Text(text = numeros, style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = {
            scope.launch {
                try {
                    val lista = RetrofitClient.api.obtenerLoteria()
                    numeros = "Números ganadores:\n$lista"
                } catch (e: Exception) {
                    numeros = "Error: ${e.message}"
                }
            }
        }) {
            Text("Generar Números")
        }
        Spacer(modifier = Modifier.height(32.dp))
        OutlinedButton(onClick = onBack) { Text("Volver") }
    }
}

@Composable
fun AdivinaScreen(onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    var numeroUsuario by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("Ingresa un número") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("ADIVINA EL NÚMERO", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = numeroUsuario,
            onValueChange = { numeroUsuario = it },
            label = { Text("Tu número") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            scope.launch {
                try {
                    val num = numeroUsuario.toIntOrNull()
                    if (num != null) {
                        val respuesta = RetrofitClient.api.adivinaNumero(num)
                        mensaje = respuesta
                    }
                } catch (e: Exception) {
                    mensaje = "Error conexión"
                }
            }
        }) { Text("Probar") }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = mensaje)
        Spacer(modifier = Modifier.height(32.dp))
        OutlinedButton(onClick = onBack) { Text("Volver") }
    }
}

@Composable
fun ParImparScreen(onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    var numeroUsuario by remember { mutableStateOf("") }
    var resultado by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("PAR O IMPAR", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = numeroUsuario,
            onValueChange = { numeroUsuario = it },
            label = { Text("Número a verificar") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            scope.launch {
                try {
                    val num = numeroUsuario.toIntOrNull()
                    if (num != null) {
                        val respuesta = RetrofitClient.api.jugarParImpar(num)
                        resultado = respuesta
                    }
                } catch (e: Exception) {
                    resultado = "Error conexión"
                }
            }
        }) { Text("Verificar") }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = resultado, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(32.dp))
        OutlinedButton(onClick = onBack) { Text("Volver") }
    }
}

@Composable
fun BlackjackScreen(onBack: () -> Unit) {
    val scope = rememberCoroutineScope()

    var puntaje by remember { mutableIntStateOf(0) }
    var cartasTexto by remember { mutableStateOf("") }
    var mensajeFinal by remember { mutableStateOf("") }
    var juegoTerminado by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(" BLACKJACK 21 ", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(30.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Tus Cartas:", fontWeight = FontWeight.Bold)
                Text(if (cartasTexto.isEmpty()) "Empieza a jugar..." else cartasTexto)
                Spacer(modifier = Modifier.height(10.dp))
                Text("Puntaje Total: $puntaje", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (mensajeFinal.isNotEmpty()) {
            Text(
                text = mensajeFinal,
                style = MaterialTheme.typography.headlineSmall,
                color = if (mensajeFinal.contains("GANASTE") || mensajeFinal.contains("Empate")) Color(0xFF2E7D32) else Color(0xFFC62828),
                modifier = Modifier.padding(8.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (!juegoTerminado) {
            Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                Button(onClick = {
                    scope.launch {
                        try {
                            val respuesta = RetrofitClient.api.pedirCarta()
                            val partes = respuesta.split("|")
                            val nombreCarta = partes[0]
                            val valorCarta = partes[1].toInt()

                            cartasTexto += "$nombreCarta\n"
                            puntaje += valorCarta

                            if (puntaje > 21) {
                                mensajeFinal = "Te pasaste de 21! Pierdes."
                                juegoTerminado = true
                            }
                        } catch (e: Exception) {
                            mensajeFinal = "Error: Prende el backend."
                        }
                    }
                }) {
                    Text("Pedir Carta")
                }

                Button(
                    onClick = {
                        scope.launch {
                            try {
                                val resultado = RetrofitClient.api.plantarse(puntaje)
                                mensajeFinal = resultado
                                juegoTerminado = true
                            } catch (e: Exception) {
                                mensajeFinal = "Error de conexión"
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                ) {
                    Text("Plantarse")
                }
            }
        } else {
            Button(
                onClick = {
                    puntaje = 0
                    cartasTexto = ""
                    mensajeFinal = ""
                    juegoTerminado = false
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Jugar Otra Vez")
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
        OutlinedButton(onClick = onBack) { Text("Volver al Menú") }
    }
}
