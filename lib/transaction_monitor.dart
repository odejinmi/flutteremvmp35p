// To parse this JSON data, do
//
//     final transactionMonitor = transactionMonitorFromJson(jsonString);

import 'dart:convert';

TransactionMonitor transactionMonitorFromJson(String str) => TransactionMonitor.fromJson(json.decode(str));

String transactionMonitorToJson(TransactionMonitor data) => json.encode(data.toJson());

class TransactionMonitor {
  String state;
  String message;
  bool status;
  TransactionData? transactionData;

  TransactionMonitor({
    required this.state,
    required this.message,
    required this.status,
    this.transactionData,
  });

  factory TransactionMonitor.fromJson(Map<String, dynamic> json) => TransactionMonitor(
    state: json["state"],
    message: json["message"],
    status: json["status"],
    transactionData: json["transactionData"].isNotEmpty?TransactionData.fromJson(json["transactionData"]):null,
  );

  Map<String, dynamic> toJson() => {
    "state": state,
    "message": message,
    "status": status,
    "transactionData": transactionData?.toJson(),
  };

  @override
  String toString()=> '"state": $state, "message": $message, "status": $status, "transactionData": ${transactionData?.toString()}';
}

class TransactionData {
  String amountAuthorized;
  String applicationDiscretionaryData;
  String applicationInterchangeProfile;
  String applicationIssuerData;
  String applicationPanSequenceNumber;
  String applicationPrimaryAccountNumber;
  String applicationTransactionCounter;
  String applicationVersionNumber;
  String authorizationResponseCode;
  String cardHolderName;
  String cardScheme;
  String cardSeqenceNumber;
  String cardholderVerificationMethod;
  String cashBackAmount;
  String cryptogram;
  String cryptogramInformationData;
  String dedicatedFileName;
  String deviceSerialNumber;
  String dencryptedPinBlock;
  String expirationDate;
  String iccDataString;
  String interfaceDeviceSerialNumber;
  String issuerApplicationData;
  String nibssIccSubset;
  String originalDeviceSerial;
  String originalPan;
  String pinBlock;
  String pinBlockDukpt;
  String pinBlockTrippleDes;
  String plainPinKey;
  String terminalCapabilities;
  String terminalCountryCode;
  String terminalType;
  String terminalVerificationResults;
  String track2Data;
  String transactionCurrencyCode;
  String transactionDate;
  String transactionSequenceCounter;
  String transactionSequenceNumber;
  String transactionType;
  String unifiedPaymentIccData;
  String unpredictableNumber;

  TransactionData({
    required this.amountAuthorized,
    required this.applicationDiscretionaryData,
    required this.applicationInterchangeProfile,
    required this.applicationIssuerData,
    required this.applicationPanSequenceNumber,
    required this.applicationPrimaryAccountNumber,
    required this.applicationTransactionCounter,
    required this.applicationVersionNumber,
    required this.authorizationResponseCode,
    required this.cardHolderName,
    required this.cardScheme,
    required this.cardSeqenceNumber,
    required this.cardholderVerificationMethod,
    required this.cashBackAmount,
    required this.cryptogram,
    required this.cryptogramInformationData,
    required this.dedicatedFileName,
    required this.deviceSerialNumber,
    required this.dencryptedPinBlock,
    required this.expirationDate,
    required this.iccDataString,
    required this.interfaceDeviceSerialNumber,
    required this.issuerApplicationData,
    required this.nibssIccSubset,
    required this.originalDeviceSerial,
    required this.originalPan,
    required this.pinBlock,
    required this.pinBlockDukpt,
    required this.pinBlockTrippleDes,
    required this.plainPinKey,
    required this.terminalCapabilities,
    required this.terminalCountryCode,
    required this.terminalType,
    required this.terminalVerificationResults,
    required this.track2Data,
    required this.transactionCurrencyCode,
    required this.transactionDate,
    required this.transactionSequenceCounter,
    required this.transactionSequenceNumber,
    required this.transactionType,
    required this.unifiedPaymentIccData,
    required this.unpredictableNumber,
  });

  factory TransactionData.fromJson(Map<String, dynamic> json) => TransactionData(
    amountAuthorized: json["amountAuthorized"],
    applicationDiscretionaryData: json["applicationDiscretionaryData"],
    applicationInterchangeProfile: json["applicationInterchangeProfile"],
    applicationIssuerData: json["applicationIssuerData"],
    applicationPanSequenceNumber: json["applicationPANSequenceNumber"],
    applicationPrimaryAccountNumber: json["applicationPrimaryAccountNumber"],
    applicationTransactionCounter: json["applicationTransactionCounter"],
    applicationVersionNumber: json["applicationVersionNumber"],
    authorizationResponseCode: json["authorizationResponseCode"],
    cardHolderName: json["cardHolderName"],
    cardScheme: json["cardScheme"],
    cardSeqenceNumber: json["cardSeqenceNumber"],
    cardholderVerificationMethod: json["cardholderVerificationMethod"],
    cashBackAmount: json["cashBackAmount"],
    cryptogram: json["cryptogram"],
    cryptogramInformationData: json["cryptogramInformationData"],
    dedicatedFileName: json["dedicatedFileName"],
    deviceSerialNumber: json["deviceSerialNumber"],
    dencryptedPinBlock: json["dencryptedPinBlock"],
    expirationDate: json["expirationDate"],
    iccDataString: json["iccDataString"],
    interfaceDeviceSerialNumber: json["interfaceDeviceSerialNumber"],
    issuerApplicationData: json["issuerApplicationData"],
    nibssIccSubset: json["nibssIccSubset"],
    originalDeviceSerial: json["originalDeviceSerial"],
    originalPan: json["originalPan"],
    pinBlock: json["pinBlock"],
    pinBlockDukpt: json["pinBlockDUKPT"],
    pinBlockTrippleDes: json["pinBlockTrippleDES"],
    plainPinKey: json["plainPinKey"],
    terminalCapabilities: json["terminalCapabilities"],
    terminalCountryCode: json["terminalCountryCode"],
    terminalType: json["terminalType"],
    terminalVerificationResults: json["terminalVerificationResults"],
    track2Data: json["track2Data"],
    transactionCurrencyCode: json["transactionCurrencyCode"],
    transactionDate: json["transactionDate"],
    transactionSequenceCounter: json["transactionSequenceCounter"],
    transactionSequenceNumber: json["transactionSequenceNumber"],
    transactionType: json["transactionType"],
    unifiedPaymentIccData: json["unifiedPaymentIccData"],
    unpredictableNumber: json["unpredictableNumber"],
  );

  Map<String, dynamic> toJson() => {
    "amountAuthorized": amountAuthorized,
    "applicationDiscretionaryData": applicationDiscretionaryData,
    "applicationInterchangeProfile": applicationInterchangeProfile,
    "applicationIssuerData": applicationIssuerData,
    "applicationPANSequenceNumber": applicationPanSequenceNumber,
    "applicationPrimaryAccountNumber": applicationPrimaryAccountNumber,
    "applicationTransactionCounter": applicationTransactionCounter,
    "applicationVersionNumber": applicationVersionNumber,
    "authorizationResponseCode": authorizationResponseCode,
    "cardHolderName": cardHolderName,
    "cardScheme": cardScheme,
    "cardSeqenceNumber": cardSeqenceNumber,
    "cardholderVerificationMethod": cardholderVerificationMethod,
    "cashBackAmount": cashBackAmount,
    "cryptogram": cryptogram,
    "cryptogramInformationData": cryptogramInformationData,
    "dedicatedFileName": dedicatedFileName,
    "deviceSerialNumber": deviceSerialNumber,
    "dencryptedPinBlock": dencryptedPinBlock,
    "expirationDate": expirationDate,
    "iccDataString": iccDataString,
    "interfaceDeviceSerialNumber": interfaceDeviceSerialNumber,
    "issuerApplicationData": issuerApplicationData,
    "nibssIccSubset": nibssIccSubset,
    "originalDeviceSerial": originalDeviceSerial,
    "originalPan": originalPan,
    "pinBlock": pinBlock,
    "pinBlockDUKPT": pinBlockDukpt,
    "pinBlockTrippleDES": pinBlockTrippleDes,
    "plainPinKey": plainPinKey,
    "terminalCapabilities": terminalCapabilities,
    "terminalCountryCode": terminalCountryCode,
    "terminalType": terminalType,
    "terminalVerificationResults": terminalVerificationResults,
    "track2Data": track2Data,
    "transactionCurrencyCode": transactionCurrencyCode,
    "transactionDate": transactionDate,
    "transactionSequenceCounter": transactionSequenceCounter,
    "transactionSequenceNumber": transactionSequenceNumber,
    "transactionType": transactionType,
    "unifiedPaymentIccData": unifiedPaymentIccData,
    "unpredictableNumber": unpredictableNumber,
  };

  @override String toString ()=> '"amountAuthorized": $amountAuthorized, "applicationDiscretionaryData": $applicationDiscretionaryData, "applicationInterchangeProfile": $applicationInterchangeProfile, '
      '"applicationIssuerData": $applicationIssuerData, "applicationPANSequenceNumber": $applicationPanSequenceNumber, "applicationPrimaryAccountNumber": $applicationPrimaryAccountNumber, '
      '"applicationTransactionCounter": $applicationTransactionCounter, "applicationVersionNumber": $applicationVersionNumber, "authorizationResponseCode": $authorizationResponseCode, '
      '"cardHolderName": $cardHolderName, "cardScheme": $cardScheme, "cardSeqenceNumber": $cardSeqenceNumber, "cardholderVerificationMethod": $cardholderVerificationMethod, "cashBackAmount": $cashBackAmount, '
      '"cryptogram": $cryptogram, "cryptogramInformationData": $cryptogramInformationData, "dedicatedFileName": $dedicatedFileName, "deviceSerialNumber": $deviceSerialNumber, "dencryptedPinBlock": $dencryptedPinBlock, '
      '"expirationDate": $expirationDate, "iccDataString": $iccDataString, "interfaceDeviceSerialNumber": $interfaceDeviceSerialNumber, "issuerApplicationData": $issuerApplicationData, "nibssIccSubset": $nibssIccSubset, '
      '"originalDeviceSerial": $originalDeviceSerial, "originalPan": $originalPan, "pinBlock": $pinBlock, "pinBlockDUKPT": $pinBlockDukpt, "pinBlockTrippleDES": $pinBlockTrippleDes, "plainPinKey": $plainPinKey, '
      '"terminalCapabilities": $terminalCapabilities, "terminalCountryCode": $terminalCountryCode, "terminalType": $terminalType, "terminalVerificationResults": $terminalVerificationResults, "track2Data": $track2Data, '
      '"transactionCurrencyCode": $transactionCurrencyCode, "transactionDate": $transactionDate, "transactionSequenceCounter": $transactionSequenceCounter, "transactionSequenceNumber": $transactionSequenceNumber, '
      '"transactionType": $transactionType, "unifiedPaymentIccData": $unifiedPaymentIccData, "unpredictableNumber": $unpredictableNumber,';
}
