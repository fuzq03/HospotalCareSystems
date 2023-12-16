package com.xyx.travelingshare.entity

data class Appointment(
    val id: Int,
    val date: String,
    val start_time: String,
    val end_time: String,
    val userid: Int,
    val username: String,
    val friendid: Int,
    val friendname: String
)
