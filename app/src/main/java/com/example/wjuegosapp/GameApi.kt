package com.example.wjuegosapp

import retrofit2.http.GET
import retrofit2.http.Query

interface GameApi {

    @GET("/loteria")
    suspend fun obtenerLoteria(): List<Int>

    @GET("/adivina")
    suspend fun adivinaNumero(@Query("numero") numero: Int): String

    @GET("/par-impar")
    suspend fun jugarParImpar(@Query("numero") numero: Int): String

    @GET("/carta")
    suspend fun pedirCarta(): String

    @GET("/jugar-dealer")
    suspend fun plantarse(@Query("puntajeJugador") puntos: Int): String
}