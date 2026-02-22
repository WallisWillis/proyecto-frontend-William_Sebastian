package com.example.wjuegosapp

import android.os.Bundle
import androidx.compose.animation.animateContentSize
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = darkColorScheme()) {
                AppNavigation()
            }
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
fun GameScreenLayout(title: String, onBack: () -> Unit, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                content()
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Volver al Menú Principal")
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
            .padding(32.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("ARCADE", fontSize = 40.sp, fontWeight = FontWeight.Black)
        Text("Selecciona un juego", color = Color.Gray)
        Spacer(modifier = Modifier.height(40.dp))

        MenuButton("Jugar Lotería", MaterialTheme.colorScheme.primary, onNavigateToLoteria)
        MenuButton("Adivina el Número", MaterialTheme.colorScheme.secondary, onNavigateToAdivina)
        MenuButton("Par o Impar", MaterialTheme.colorScheme.tertiary, onNavigateToParImpar)
        MenuButton("Blackjack (21)", Color(0xFF2E7D32), onNavigateToBlackjack)
    }
}

@Composable
fun MenuButton(text: String, color: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .height(60.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color)
    ) {
        Text(text, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun LoteriaScreen(onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    var numeros by remember { mutableStateOf("Toca el botón para tu suerte") }

    GameScreenLayout(title = "LOTERÍA", onBack = onBack) {
        Text(numeros, fontSize = 20.sp, textAlign = TextAlign.Center, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                scope.launch {
                    try {
                        val lista = RetrofitClient.api.obtenerLoteria()
                        numeros = "Tus números ganadores:\n\n$lista"
                    } catch (e: Exception) { numeros = "Error de conexión" }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) { Text("Generar Números", fontSize = 16.sp) }
    }
}

@Composable
fun AdivinaScreen(onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    var numeroUsuario by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("Adivina un número del 1 al 20") }

    GameScreenLayout(title = "ADIVINA", onBack = onBack) {
        OutlinedTextField(
            value = numeroUsuario,
            onValueChange = { numeroUsuario = it },
            label = { Text("Tu número") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                scope.launch {
                    try {
                        val num = numeroUsuario.toIntOrNull()
                        if (num != null) {
                            mensaje = RetrofitClient.api.adivinaNumero(num)
                            numeroUsuario = ""
                        }
                    } catch (e: Exception) { mensaje = "Error de conexión" }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) { Text("Probar Suerte") }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            mensaje,
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            color = if (mensaje.contains("Ganaste")) Color(0xFF4CAF50) else Color.White
        )
    }
}

@Composable
fun ParImparScreen(onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    var numeroUsuario by remember { mutableStateOf("") }
    var resultado by remember { mutableStateOf("Ingresa un número para evaluar") }

    GameScreenLayout(title = "PAR / IMPAR", onBack = onBack) {
        OutlinedTextField(
            value = numeroUsuario,
            onValueChange = { numeroUsuario = it },
            label = { Text("Número a verificar") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                scope.launch {
                    try {
                        val num = numeroUsuario.toIntOrNull()
                        if (num != null) resultado = RetrofitClient.api.jugarParImpar(num)
                    } catch (e: Exception) { resultado = "Error conexión" }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) { Text("Verificar") }
        Spacer(modifier = Modifier.height(16.dp))
        Text(resultado, fontSize = 20.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
    }
}

@Composable
fun BlackjackScreen(onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    var puntaje by remember { mutableIntStateOf(0) }
    var cartasTexto by remember { mutableStateOf("") }
    var mensajeFinal by remember { mutableStateOf("") }
    var juegoTerminado by remember { mutableStateOf(false) }

    GameScreenLayout(title = "♠️ BLACKJACK", onBack = onBack) {
        Text("Tus Cartas:", color = Color.Gray)
        Text(if (cartasTexto.isEmpty()) "Mesa limpia" else cartasTexto, fontSize = 18.sp, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Puntaje Total: $puntaje", fontSize = 28.sp, fontWeight = FontWeight.Black)
        Spacer(modifier = Modifier.height(16.dp))

        if (mensajeFinal.isNotEmpty()) {
            Text(
                text = mensajeFinal,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = if (mensajeFinal.contains("GANASTE") || mensajeFinal.contains("Empate")) Color(0xFF4CAF50) else Color(0xFFF44336)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (!juegoTerminado) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        scope.launch {
                            try {
                                val respuesta = RetrofitClient.api.pedirCarta()
                                val partes = respuesta.split("|")
                                cartasTexto += "${partes[0]}\n"
                                puntaje += partes[1].toInt()
                                if (puntaje > 21) {
                                    mensajeFinal = "¡Te pasaste de 21! Pierdes."
                                    juegoTerminado = true
                                }
                            } catch (e: Exception) { mensajeFinal = "Error de red" }
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) { Text("Pedir") }

                Button(
                    onClick = {
                        scope.launch {
                            try {
                                mensajeFinal = RetrofitClient.api.plantarse(puntaje)
                                juegoTerminado = true
                            } catch (e: Exception) { mensajeFinal = "Error de red" }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF9800),
                        contentColor = Color.Black
                    ),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) { Text("Plantarse") }
            }
        } else {
            Button(
                onClick = {
                    puntaje = 0
                    cartasTexto = ""
                    mensajeFinal = ""
                    juegoTerminado = false
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) { Text("Jugar Otra Vez") }
        }
    }
}