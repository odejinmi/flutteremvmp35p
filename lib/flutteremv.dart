import 'package:flutter/material.dart';
import 'package:flutteremv/print.dart';
import 'package:flutteremv/transaction_monitor.dart';

import 'flutteremv_platform_interface.dart';

class Flutteremv {
  Stream<dynamic> get stateStream {
    return FlutteremvPlatform.instance.stateStream;
  }

  Future<String?> getPlatformVersion() {
    return FlutteremvPlatform.instance.getPlatformVersion();
  }

  Future<String?> deviceserialnumber() {
    return FlutteremvPlatform.instance.deviceserialnumber();
  }

  Future<TransactionMonitor> initialize(
    String masterKey,
    String terminalId,
    String transactionCounter,
  ) async {
    return FlutteremvPlatform.instance.initialize(
      masterKey,
      terminalId,
      transactionCounter,
    );
  }

  void debitcard(String amount) async {
    FlutteremvPlatform.instance.debitcard(amount);
  }

  void enterpin(String amount) async {
    return FlutteremvPlatform.instance.enterpin(amount);
  }

  void cancelcardprocess() async {
    return FlutteremvPlatform.instance.cancelcardprocess();
  }

  void startkeyboard({
    ValueChanged<String>? onchange,
    Function? proceed,
    Function? cancel,
  }) async {
    return FlutteremvPlatform.instance.startkeyboard(
      onchange: onchange,
      proceed: proceed,
      cancel: cancel,
    );
  }

  void startpinpad({
    ValueChanged<String>? onchange,
    Function? proceed,
    Function? cancel,
  }) async {
    return FlutteremvPlatform.instance.startpinpad(
      onchange: onchange,
      proceed: proceed,
      cancel: cancel,
    );
  }

  void stopkeyboard() async {
    return FlutteremvPlatform.instance.stopkeyboard();
  }

  Future<TransactionMonitor> getcardsheme(String amount) async {
    return FlutteremvPlatform.instance.getcardsheme(amount);
  }

  Future<TransactionMonitor> startprinting(Print print) async {
    return FlutteremvPlatform.instance.startprinting(print);
  }

  Future<TransactionMonitor> starteodPrint(
    Map<String, dynamic> template,
  ) async {
    return FlutteremvPlatform.instance.starteodPrint(template);
  }

  Future<TransactionMonitor> startprintjson(
    List<Map<String, Object>> template,
  ) async {
    return FlutteremvPlatform.instance.startprintjson(template);
  }

  Future<TransactionMonitor> startcustomprinting(List<Widget> template) async {
    return FlutteremvPlatform.instance.startcustomprinting(template);
  }
}
