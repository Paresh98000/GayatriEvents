package com.pareshkumarsharma.gayatrievents.api.model

data class EventRegistrationModel(
    val EventPriceList: String,
    val ServiceGlobalIdList: String,
    val ServiceProductGlobalIdList: String,
    val InputFieldValues: MutableMap<String, MutableMap<String,String>>
)