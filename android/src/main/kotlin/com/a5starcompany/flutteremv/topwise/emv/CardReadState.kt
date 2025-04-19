package com.a5starcompany.flutteremv.topwise.emv

enum class CardReadState {
    Loading,
    CardData,
    CardReadTimeOut,
    CallBackError,
    CallBackCanceled,
    CallBackTransResult,
    CardDetected,
}




