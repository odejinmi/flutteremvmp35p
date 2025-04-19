import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:flutteremv/flutteremv.dart';
import 'package:flutteremv/printmodel.dart';

import 'eodmodel.dart';

class Eod extends StatefulWidget {
  const Eod({Key? key}) : super(key: key);

  @override
  _EodState createState() => _EodState();
}

class _EodState extends State<Eod> {
  final _topwisePlugin = Flutteremv();
  var server = {
    "success": true,
    "message": "Fetched",
    "data": [
      {
        "id": 94,
        "business_id": 11,
        "user_id": 40,
        "uuid": "449925957525571875",
        "reference": "2023033116512061789989",
        "type": "debit",
        "remark":
            "MTN N50 50MB - (24 Hours) Purchase Was Successful To 08166939205",
        "amount": "49",
        "previous": "4807.1",
        "balance": "4758.1",
        "status": 1,
        "created_at": "2023-03-31T16:51:37.000000Z",
        "updated_at": "2023-03-31T16:51:37.000000Z",
      },
      {
        "id": 93,
        "business_id": 11,
        "user_id": 40,
        "uuid": "449925957525571875",
        "reference": "202303311651363599982",
        "type": "debit",
        "remark": "NGN 100 MTN Airtime Purchase Was Successful To 08166939205",
        "amount": "100",
        "previous": "4905.1",
        "balance": "4807.1",
        "status": 1,
        "created_at": "2023-03-31T16:51:16.000000Z",
        "updated_at": "2023-03-31T16:51:16.000000Z",
      },
    ],
    "income": "0",
    "expenses": "149",
  };
  late Column child;
  @override
  void initState() {
    // TODO: implement initState
    super.initState();
    Eodmodel eodmodel = eodmodelFromJson(jsonEncode(server));
    child = Column(
      children: [
        for (int i = 0; i < eodmodel.data.length; i++) item(eodmodel.data[i]),
      ],
    );
    // child = ListView.builder(
    //   padding: const EdgeInsets.all(8.0),
    //   itemBuilder: (context, position) {
    //     return item(eodmodel.data[position]);
    //   },
    //   itemCount: eodmodel.data.length,
    // );
    ListView(children: []);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: SafeArea(
        child: Container(
          margin: const EdgeInsets.symmetric(horizontal: 20),
          child: SingleChildScrollView(
            child: Column(
              children: [
                child,
                ElevatedButton(
                  onPressed: () async {
                    // printcomponent(child);
                    _topwisePlugin.startcustomprinting(child.children).then((
                      value,
                    ) {
                      print(value);
                    });
                  },
                  child: const Text("Eod printing"),
                ),
                SizedBox(height: 30),
              ],
            ),
          ),
        ),
      ),
    );
  }

  Widget item(nDatalist) {
    return Container(
      margin: const EdgeInsets.symmetric(vertical: 10),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              Expanded(
                child: Text(
                  "${nDatalist.remark}",
                  style: const TextStyle(
                    fontWeight: FontWeight.w500,
                    fontSize: 16,
                    color: Color(0xff303030),
                  ),
                ),
              ),
              Text(
                "₦ ${nDatalist.amount}",
                style: const TextStyle(
                  fontWeight: FontWeight.w700,
                  fontSize: 15,
                  color: Color(0xff4A484B),
                ),
              ),
            ],
          ),
          const SizedBox(height: 10),
          Text(
            "${nDatalist.createdAt}",
            style: const TextStyle(
              fontWeight: FontWeight.w500,
              fontSize: 12,
              color: Color(0xff6D6D6D),
            ),
          ),
          const Divider(),
          const SizedBox(height: 10),
        ],
      ),
    );
  }

  Future<List<Printmodel>> printcomponent(Widget children) async {
    List<Printmodel> generalprintvalue = [];
    if (children is ListView) {
      print(children.semanticChildCount);
      print(children.childrenDelegate.estimatedChildCount);
    } else if (children is Column) {
      print(children.children);
    }
    // for(Widget i in children){
    //   if (i is Row) {
    //     generalprintvalue.add(Printmodel(data: await rowcol(i.children)));
    //   }else if (i is Expanded) {
    //     generalprintvalue.add(Printmodel(data:[await item(i.child)]));
    //   }else{
    //     generalprintvalue.add(Printmodel(data:[await item(i)]));
    //   }
    // }
    return generalprintvalue;
  }
}
