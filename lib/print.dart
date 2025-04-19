class Print {
  Print({
    required this.base64image,
    required this.marchantname,
    required this.marchantaddress,
    required this.serialno,
    this.deviceid,
    required this.transactiontype,
    required this.transactionstatus,
    required this.terminalid,
    required this.amount,
    required this.rrn,
    this.stan,
    required this.bottommessage,
    required this.datetime,
    this.merchantid,
    this.accountname,
    this.businessaccountname,
    required this.copytype,
    this.pan,
    this.devicetype,
    this.paymentname,
    this.paymentcode,
    this.phonenumber,
    this.network,
    this.description,
    this.disco,
    this.meteraccname,
    this.meterno,
    this.token,
    this.unit,
    this.address,
    this.expiry,
    this.accountnumber,
    this.bank,
    this.businessaccountnumber,
    this.businessbank,
    this.sessionid,
    this.responsecode,
    this.message,
    this.appversion,
  });

  String base64image;
  String marchantname;
  String datetime;
  String serialno;
  String terminalid;
  String? merchantid;
  String marchantaddress;
  String? deviceid;
  String transactiontype;
  String? accountname;
  String? businessaccountname;
  String copytype;
  String rrn;
  String? stan;
  String? pan;
  String? devicetype;
  String? paymentname;
  String? paymentcode;
  String? phonenumber;
  String? network;
  String? description;
  String? disco;
  String? meteraccname;
  String? meterno;
  String? token;
  String? unit;
  String? address;
  String? expiry;
  String? accountnumber;
  String? bank;
  String? businessaccountnumber;
  String? businessbank;
  String? sessionid;
  String transactionstatus;
  String? responsecode;
  String? message;
  String? appversion;
  String amount;
  String bottommessage;

  factory Print.fromJson(Map<String, dynamic> json) => Print(
    base64image: json["base64image"],
    marchantname: json["marchantname"],
    datetime: json["datetime"],
    terminalid: json["terminalid"],
    serialno: json["serialno"],
    merchantid: json["merchantid"],
    transactiontype: json["transactiontype"],
    accountname: json["accountname"],
    businessaccountname: json["businessaccountname"],
    marchantaddress: json["marchantaddress"],
    deviceid: json["deviceid"],
    copytype: json["copytype"],
    rrn: json["rrn"],
    stan: json["stan"],
    pan: json["pan"],
    devicetype: json["devicetype"],
    paymentcode: json["paymentcode"],
    paymentname: json["paymentname"],
    phonenumber: json["phonenumber"],
    network: json["network"],
    description: json["description"],
    disco: json["disco"],
    meteraccname: json["meteraccname"],
    meterno: json["meterno"],
    token: json["token"],
    unit: json["unit"],
    address: json["address"],
    expiry: json["expiry"],
    accountnumber: json["accountnumber"],
    bank: json["bank"],
    businessaccountnumber: json["businessaccountnumber"],
    businessbank: json["businessbank"],
    sessionid: json["sessionid"],
    transactionstatus: json["transactionstatus"],
    responsecode: json["responsecode"],
    message: json["message"],
    appversion: json["appversion"],
    amount: json["amount"],
    bottommessage: json["bottommessage"],
  );

  Map<String, dynamic> toJson() => {
    "base64image": base64image,
    "marchantname": marchantname,
    "datetime": datetime,
    "terminalid": terminalid,
    "merchantid": merchantid,
    "transactiontype": transactiontype,
    "accountname": accountname,
    "businessaccountname": businessaccountname,
    "copytype": copytype,
    "marchantaddress": marchantaddress,
    "deviceid": deviceid,
    "rrn": rrn,
    "serialno": serialno,
    "stan": stan,
    "devicetype": devicetype,
    "paymentname": paymentname,
    "paymentcode": paymentcode,
    "phonenumber": phonenumber,
    "network": network,
    "description": description,
    "disco": disco,
    "meteraccname": meteraccname,
    "meterno": meterno,
    "token": token,
    "unit": unit,
    "address": address,
    "pan": pan,
    "expiry": expiry,
    "accountnumber": accountnumber,
    "bank": bank,
    "businessaccountnumber": businessaccountnumber,
    "businessbank": businessbank,
    "sessionid": sessionid,
    "transactionstatus": transactionstatus,
    "responsecode": responsecode,
    "message": message,
    "appversion": appversion,
    "amount": amount,
    "bottommessage": bottommessage,
  };
}
