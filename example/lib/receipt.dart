import 'package:flutter/material.dart';
import 'package:flutteremv/flutteremv.dart';

class Receipt extends StatelessWidget {
  Receipt({Key? key}) : super(key: key);

  final _topwisePlugin = Flutteremv();
  Column child = Column(
    children: [
      Image.asset("asset/logo2.png", height: 60, width: 30),
      const SizedBox(height: 30),
      const Text("Transaction Receipt", textAlign: TextAlign.center),
      const SizedBox(height: 20),
      Row(
        children: const [
          Expanded(child: Text("Agent Name")),
          Expanded(
            child: Text(
              "Samuel Diamond",
              style: TextStyle(fontWeight: FontWeight.bold),
            ),
          ),
        ],
      ),
      const SizedBox(height: 20),
      Row(
        children: const [
          Expanded(child: Text("Reference")),
          Expanded(
            child: Text(
              "2023032807184162745",
              style: TextStyle(fontWeight: FontWeight.bold),
            ),
          ),
        ],
      ),
      const SizedBox(height: 20),
      Row(
        children: const [
          Expanded(child: Text("Amount")),
          Expanded(
            child: Text(
              "NGN 147.00",
              style: TextStyle(fontWeight: FontWeight.bold),
            ),
          ),
        ],
      ),
      const SizedBox(height: 30),
      Row(
        children: const [
          Expanded(child: Text("Remark")),
          Expanded(
            child: Text(
              "MTN N150 160MB - 30 days Purchase "
              "Was Successful To 08166939205",
              style: TextStyle(fontWeight: FontWeight.bold),
            ),
          ),
        ],
      ),
      const SizedBox(height: 30),
      Row(
        children: const [
          Expanded(child: Text("Transaction Date")),
          Expanded(
            child: Text(
              "2023-03-28 07:18:18.000Z",
              style: TextStyle(fontWeight: FontWeight.bold),
            ),
          ),
        ],
      ),
      const SizedBox(height: 40),
      Row(
        children: const [
          Expanded(child: Text("Transaction Status")),
          Expanded(
            child: Text(
              "Successful",
              style: TextStyle(fontWeight: FontWeight.bold),
            ),
          ),
        ],
      ),
      const SizedBox(height: 50),
    ],
  );
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: SafeArea(
        child: SingleChildScrollView(
          child: Container(
            margin: const EdgeInsets.symmetric(horizontal: 20),
            child: Column(
              children: [
                child,
                ElevatedButton(
                  onPressed: () async {
                    // var printvalue = [
                    //   {"data":[{"image":"base64string","align":"center","imagewidth":30, "imageheight":30}]},
                    //   {"data":[{"text":"MERCHANT NAME", "textsize":"normal","align":"center"}]},
                    //   {"text":[{"text":"Tolulope", "textsize":"normal","align":"center"}]},
                    //   {"text":[{"text":"DATE/TIME", "textsize":"normal","align":"left"},{"text":"tolulope", "textsize":"normal","align":"right"}]},
                    //   {"text":[{"text":"TERMINAL ID", "textsize":"normal","align":"left"},{"text":"tolulope", "textsize":"normal","align":"right"}]},
                    //   {"text":[{"text":"MERCHANT ID", "textsize":"normal","align":"left"},{"text":"tolulope", "textsize":"normal","align":"right"}]},
                    //   {"text":[{"text":"", "textsize":"normal","align":"center"}]},
                    //   {"text":[{"text":"*****************************************************"}]},
                    //   {"text":[{"text":"transactiontype", "textsize":"normal","align":"center"}]},
                    //   {"text":[{"text":"accountname", "textsize":"normal","align":"center"}]},
                    //   {"text":[{"text":"Customer Copy", "textsize":"normal","align":"center"}]},
                    //   {"text":[{"text":"*****************************************************"}]},
                    //   {"text":[{"text":"RRN:", "textsize":"normal","align":"left"},{"text":"tolulope", "textsize":"normal","align":"right"}]},
                    //   {"text":[{"text":"STAN:", "textsize":"normal","align":"left"},{"text":"tolulope", "textsize":"normal","align":"right"}]},
                    //   {"text":[{"text":"PAN:", "textsize":"normal","align":"left"},{"text":"tolulope", "textsize":"normal","align":"right"}]},
                    //   {"text":[{"text":"CARD EXPIRY", "textsize":"normal","align":"left"},{"text":"tolulope", "textsize":"normal","align":"right"}]},
                    //   {"text":[{"text":"ACCOUNT NUMBER:", "textsize":"normal","align":"left"},{"text":"tolulope", "textsize":"normal","align":"right"}]},
                    //   {"text":[{"text":"BANK", "textsize":"normal","align":"left"},{"text":"tolulope", "textsize":"normal","align":"right"}]},
                    //   {"text":[{"text":"transactionstatus", "textsize":"normal","align":"center"}]},
                    //   {"text":[{"text":"Response code", "textsize":"normal","align":"left"},{"text":"tolulope", "textsize":"normal","align":"right"}]},
                    //   {"text":[{"text":"Message", "textsize":"normal","align":"left"},{"text":"tolulope", "textsize":"normal","align":"right"}]},
                    //   {"text":[{"text":"App Version", "textsize":"normal","align":"left"},{"text":"tolulope", "textsize":"normal","align":"right"}]},
                    //   {"text":[{"text":"", "textsize":"normal","align":"center"}]},
                    //   {"text":[{"text":"*****************************************************"}]},
                    //   {"text":[{"text":"₦2000.00", "textsize":"normal","align":"center"}]},
                    //   {"text":[{"text":"*****************************************************"}]},
                    //   {"text":[{"text":"bottommessage", "textsize":"normal","align":"center"}]},
                    //   {"text":[{"text":"*****************************************************"}]},
                    //   {"text":[{"text":"", "textsize":"normal","align":"center"}]},
                    //   {"text":[{"text":"", "textsize":"normal","align":"center"}]},
                    //   {"text":[{"text":"", "textsize":"normal","align":"center"}]},
                    // ];
                    // final List<Printmodel> printmodel = printmodelFromJson(jsonEncode(printvalue));
                    _topwisePlugin.startcustomprinting(child.children).then((
                      value,
                    ) {
                      print(value);
                    });
                  },
                  child: const Text("custom printing"),
                ),
                SizedBox(height: 30),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
