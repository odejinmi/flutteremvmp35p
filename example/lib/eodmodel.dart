// To parse this JSON data, do
//
//     final eodmodel = eodmodelFromJson(jsonString);

import 'dart:convert';

Eodmodel eodmodelFromJson(String str) => Eodmodel.fromJson(json.decode(str));

String eodmodelToJson(Eodmodel data) => json.encode(data.toJson());

class Eodmodel {
  Eodmodel({
    required this.success,
    required this.message,
    required this.data,
    required this.income,
    required this.expenses,
  });

  bool success;
  String message;
  List<Datum> data;
  String income;
  String expenses;

  factory Eodmodel.fromJson(Map<String, dynamic> json) => Eodmodel(
    success: json["success"],
    message: json["message"],
    data: List<Datum>.from(json["data"].map((x) => Datum.fromJson(x))),
    income: json["income"],
    expenses: json["expenses"],
  );

  Map<String, dynamic> toJson() => {
    "success": success,
    "message": message,
    "data": List<dynamic>.from(data.map((x) => x.toJson())),
    "income": income,
    "expenses": expenses,
  };
}

class Datum {
  Datum({
    required this.id,
    required this.businessId,
    required this.userId,
    required this.uuid,
    required this.reference,
    required this.type,
    required this.remark,
    required this.amount,
    required this.previous,
    required this.balance,
    required this.status,
    required this.createdAt,
    required this.updatedAt,
  });

  int id;
  int businessId;
  int userId;
  String uuid;
  String reference;
  String type;
  String remark;
  String amount;
  String previous;
  String balance;
  int status;
  DateTime createdAt;
  DateTime updatedAt;

  factory Datum.fromJson(Map<String, dynamic> json) => Datum(
    id: json["id"],
    businessId: json["business_id"],
    userId: json["user_id"],
    uuid: json["uuid"],
    reference: json["reference"],
    type: json["type"],
    remark: json["remark"],
    amount: json["amount"],
    previous: json["previous"],
    balance: json["balance"],
    status: json["status"],
    createdAt: DateTime.parse(json["created_at"]),
    updatedAt: DateTime.parse(json["updated_at"]),
  );

  Map<String, dynamic> toJson() => {
    "id": id,
    "business_id": businessId,
    "user_id": userId,
    "uuid": uuid,
    "reference": reference,
    "type": type,
    "remark": remark,
    "amount": amount,
    "previous": previous,
    "balance": balance,
    "status": status,
    "created_at": createdAt.toIso8601String(),
    "updated_at": updatedAt.toIso8601String(),
  };
}
