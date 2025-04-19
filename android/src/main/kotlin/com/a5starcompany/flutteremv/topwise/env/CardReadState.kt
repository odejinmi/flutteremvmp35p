package com.a5starcompany.flutteremv.emvreader.emv

enum class CardReadState {
    Loading,
    CardData,
    CardReadTimeOut,
    CallBackError,
    CallBackCanceled,
    CallBackTransResult,
    CardDetected,
}




