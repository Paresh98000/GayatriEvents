package com.pareshkumarsharma.gayatrievents.api.model

import java.util.Date

data class EventDisplayModel(
    val EventGlobalId: String,
    val EventName: String,
    val EventDateFixed: Boolean,
    val EventDateStart: String,
    val EventDateEnd: String,
    val EventPrice: Float,
    val ServiceGlobalId: String,
    val ServiceProductGlobalId: String,
    val EventDetails: String,
    val Approved: Boolean,
    val Approval_Time: String,
    val UserGlobalId: String,
    val CreationTime:String,
    val Reason:String
)
