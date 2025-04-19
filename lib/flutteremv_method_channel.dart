import 'dart:convert';
import 'dart:io';

import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutteremv/print.dart';
import 'package:flutteremv/printmodel.dart';
import 'package:flutteremv/transaction_monitor.dart';
import 'package:http/http.dart' as http;

import 'flutteremv_platform_interface.dart';
import 'handlekeyboard.dart';

/// An implementation of [FlutteremvPlatform] that uses method channels.
class MethodChannelFlutteremv extends FlutteremvPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('flutteremv');
  final eventChannel = const EventChannel('topwisemp35pevent');

  // @override
  Stream<dynamic> get stateStream {
    return eventChannel.receiveBroadcastStream();
  }

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>(
      'getPlatformVersion',
    );
    return version;
  }

  @override
  void debitcard(String amount) async {
    Map<String, String> args = {"amount": amount};
    methodChannel.invokeMethod("debitcard", args);
    // if (kDebugMode) {
    //   int max = 0;
    //   String word = "";
    //   for (final key in result["transactionData"].keys) {
    //     max++;
    //     if (max < 22) {
    //       word += '"$key":"${result["transactionData"][key]}",';
    //     } else {
    //       print("tolu result");
    //       print({word});
    //       word = '"$key":"${result["transactionData"][key]}",';
    //       max = 1;
    //     }
    //     // print('"$key":"${result["transactionData"][key]}"');
    //   }
    //
    //   print("tolu result");
    //   print({word});
    // }
    // return transactionMonitorFromJson(jsonEncode(result));
  }

  @override
  void initialize() async {
    methodChannel.invokeMethod("initialize");
    print("plugin initialize");
  }

  @override
  void enterpin(String amount) async {
    Map<String, String> args = {"pin": amount};
    methodChannel.invokeMethod("enterpin", args);
  }

  @override
  void cancelcardprocess() async {
    methodChannel.invokeMethod("cancelcardprocess");
  }

  HardwareKeyboard hardwareKeyboard = HardwareKeyboard.instance;
  late Keyevent
  keyEventHandler; // Keep a reference to the same Keyevent instance

  @override
  void startkeyboard({
    ValueChanged<String>? onchange,
    Function? proceed,
    Function? cancel,
  }) async {
    // Initialize the keyEventHandler once and use it everywhere
    keyEventHandler = Keyevent(
      onchange: onchange,
      proceed: proceed,
      cancel: cancel,
    );
    hardwareKeyboard.addHandler(keyEventHandler.handleKeyEvent);
  }

  @override
  void stopkeyboard() async {
    hardwareKeyboard.removeHandler(keyEventHandler.handleKeyEvent);
  }

  @override
  Future<TransactionMonitor> getcardsheme(String amount) async {
    Map<String, String> args = {"amount": amount};
    var result = await methodChannel.invokeMethod("getcardsheme", args);
    if (kDebugMode) {
      int max = 0;
      String word = "";
      for (final key in result["transactionData"].keys) {
        max++;
        if (max < 22) {
          word += '"$key":"${result["transactionData"][key]}",';
        } else {
          print("tolu result");
          print({word});
          word = '"$key":"${result["transactionData"][key]}",';
          max = 1;
        }
        // print('"$key":"${result["transactionData"][key]}"');
      }

      print("tolu result");
      print({word});
    }
    return transactionMonitorFromJson(jsonEncode(result));
  }

  @override
  Future<String> deviceserialnumber() async {
    // return methodChannel
    //     .invokeMethod("initializePayment", args)
    //     .then<TransactionMonitor>((dynamic result) =>
    //     TransactionMonitor.fromMap(Map<String, dynamic>.from(result)));
    return await methodChannel.invokeMethod('serialnumber');
  }

  @override
  Future<TransactionMonitor> startprinting(Print print) async {
    var result = await methodChannel.invokeMethod("startprint", print.toJson());
    return transactionMonitorFromJson(jsonEncode(result));
  }

  @override
  Future<TransactionMonitor> starteodPrint(
    Map<String, dynamic> template,
  ) async {
    var result = await methodChannel.invokeMethod("starteodPrint", template);
    return transactionMonitorFromJson(jsonEncode(result));
  }

  @override
  Future<TransactionMonitor> startprintjson(
    List<Map<String, Object>> template,
  ) async {
    Map<String, String> args = {"textprint": jsonEncode(template)};
    var result = await methodChannel.invokeMethod("startprintjson", args);
    return transactionMonitorFromJson(jsonEncode(result));
  }

  @override
  Future<TransactionMonitor> startcustomprinting(List<Widget> template) async {
    Map<String, String> args = {
      "textprint": printmodelToJson(await printcomponent(template)),
    };
    var result = await methodChannel.invokeMethod("startcustomprint", args);
    return transactionMonitorFromJson(jsonEncode(result));
  }

  Future<List<Printmodel>> printcomponent(List<Widget> children) async {
    List<Printmodel> generalprintvalue = [];
    generalprintvalue = await mutipleitem(children);
    return generalprintvalue;
  }

  Future<List<Printmodel>> mutipleitem(List<Widget> children) async {
    List<Printmodel> generalprintvalue = [];
    for (Widget i in children) {
      if (i is Row) {
        generalprintvalue.add(Printmodel(data: await rowcol(i.children)));
      } else if (i is Container) {
        if (i.child != null) {
          if (generalprintvalue.isEmpty) {
            generalprintvalue = await container(i.child!);
          } else {
            List<Printmodel> sola = await container(i.child!);
            for (int i = 0; i < sola.length; i++) {
              generalprintvalue.add(sola[i]);
            }
          }
          // generalprintvalue.add(await container(i.child!));
        } else {
          generalprintvalue.add(Printmodel(data: [await item(i)]));
        }
      } else {
        generalprintvalue.add(Printmodel(data: [await expanded(i)]));
      }
    }
    return generalprintvalue;
  }

  Future<List<Printmodel>> container(Widget i) async {
    List<Printmodel> generalprintvalue = [];
    if (i is Column) {
      if (generalprintvalue.isEmpty) {
        generalprintvalue = await mutipleitem(i.children);
      } else {
        List<Printmodel> sola = await mutipleitem(i.children);
        for (int i = 0; i < sola.length; i++) {
          generalprintvalue.add(sola[i]);
        }
      }
    } else {
      generalprintvalue.add(Printmodel(data: [await item(i)]));
    }
    return generalprintvalue;
  }

  Future<List<Datum>> rowcol(List<Widget> children) async {
    List<Datum> printvalue = [];
    for (Widget i in children) {
      printvalue.add(await expanded(i));
    }
    return printvalue;
  }

  Future<Datum> expanded(Widget i) async {
    if (i is Expanded) {
      return await item(i.child, wrap: true, flex: 3);
    } else {
      return await item(i);
    }
  }

  Future<Datum> item(Widget i, {bool? wrap, int flex = 1}) async {
    Datum datum;
    if (i is Image) {
      if (i.image is AssetImage) {
        String assetName = (i.image as AssetImage).assetName;
        final ByteData assetByteData = await rootBundle.load(assetName);
        final Uint8List imagebytes = assetByteData.buffer.asUint8List();
        datum = Datum(
          image: base64.encode(imagebytes),
          align: "center",
          imagewidth: 550,
          imageheight: 70,
        );
      } else if (i.image is NetworkImage) {
        String result;
        String imageUrl = (i.image as NetworkImage).url;
        var response = await http.get(Uri.parse(imageUrl));
        if (response.statusCode == HttpStatus.ok) {
          var bytes = response.bodyBytes;
          result = base64.encode(bytes);
        } else {
          result = "";
          throw Exception('Failed to load image: ${response.statusCode}');
        }
        datum = Datum(
          image: result,
          align: "center",
          imagewidth: 550,
          imageheight: 70,
        );
      } else if (i.image is MemoryImage) {
        Uint8List imageUrl = (i.image as MemoryImage).bytes;
        datum = Datum(
          image: base64.encode(imageUrl),
          align: "center",
          imagewidth: 550,
          imageheight: 70,
        );
      } else {
        String imageUrl = (i.image as FileImage).file.path;
        File imagefile = File(imageUrl); //convert Path to File
        Uint8List imagebytes = await imagefile.readAsBytes(); //convert to bytes
        datum = Datum(
          image: base64.encode(imagebytes),
          align: "center",
          imagewidth: 550,
          imageheight: 70,
        );
      }
    } else if (i is Text) {
      String? textalign;
      bool bold = false;

      if (i.textAlign != null) {
        textalign = i.textAlign.toString().split(".")[1];
      }
      if (i.style != null) {
        int weight = int.parse(
          i.style!.fontWeight.toString().split(".")[1].split("w")[1],
        );
        if (weight >= 700) {
          bold = true;
        }
      }
      bool? rap;
      if (wrap != null) {
        rap = wrap;
      } else if (i.softWrap != null) {
        rap = i.softWrap;
      } else {
        rap = false;
      }
      datum = Datum(
        text: i.data.toString(),
        textsize: "normal",
        align: textalign,
        textwrap: rap,
        bold: bold,
        flex: flex,
      );
    } else if (i is Divider) {
      datum = Datum(
        text:
            "-------------------------------------------------------------------------",
        textsize: "normal",
        align: "center",
        textwrap: false,
        bold: true,
      );
    } else {
      datum = Datum(text: "", textsize: "normal", align: "center");
    }
    return datum;
  }
}
