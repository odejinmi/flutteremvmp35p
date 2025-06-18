import 'package:flutter/material.dart';
import 'package:flutteremv/print.dart';
import 'package:flutteremv/transaction_monitor.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'flutteremv_method_channel.dart';

abstract class FlutteremvPlatform extends PlatformInterface {
  /// Constructs a FlutteremvPlatform.
  FlutteremvPlatform() : super(token: _token);

  static final Object _token = Object();

  static FlutteremvPlatform _instance = MethodChannelFlutteremv();

  /// The default instance of [FlutteremvPlatform] to use.
  ///
  /// Defaults to [MethodChannelFlutteremv].
  static FlutteremvPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [FlutteremvPlatform] when
  /// they register themselves.
  static set instance(FlutteremvPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }

  Stream<dynamic> get stateStream {
    throw UnimplementedError('stream has not been implemented.');
  }

  void debitcard(String amount) {
    throw UnimplementedError('debitcard() has not been implemented.');
  }

  Future<TransactionMonitor> initialize(
    String masterKey,
    String terminalId,
    String transactionCounter,
  ) {
    throw UnimplementedError('initialize() has not been implemented.');
  }

  void enterpin(String amount) {
    throw UnimplementedError('enterpin() has not been implemented.');
  }

  void cancelcardprocess() {
    throw UnimplementedError('cancelcardprocess() has not been implemented.');
  }

  void startkeyboard({
    ValueChanged<String>? onchange,
    Function? proceed,
    Function? cancel,
  }) {
    throw UnimplementedError('startkeyboard() has not been implemented.');
  }

  void startpinpad({
    ValueChanged<String>? onchange,
    Function? proceed,
    Function? cancel,
  }) {
    throw UnimplementedError('startpinpad() has not been implemented.');
  }

  void stopkeyboard() {
    throw UnimplementedError('stopkeyboard() has not been implemented.');
  }

  Future<TransactionMonitor> getcardsheme(String amount) {
    throw UnimplementedError('getcardsheme() has not been implemented.');
  }

  Future<TransactionMonitor> printreceipt(Print print) {
    throw UnimplementedError('printreceipt() has not been implemented.');
  }

  Future<String> deviceserialnumber() {
    throw UnimplementedError('deviceserialnumber() has not been implemented.');
  }

  Future<TransactionMonitor> startprinting(Print print) {
    throw UnimplementedError('startprinting() has not been implemented.');
  }

  Future<TransactionMonitor> startprintjson(
    List<Map<String, Object>> template,
  ) {
    throw UnimplementedError('startprintjson() has not been implemented.');
  }

  Future<TransactionMonitor> starteodPrint(Map<String, dynamic> template) {
    throw UnimplementedError('starteodPrint() has not been implemented.');
  }

  Future<TransactionMonitor> startcustomprinting(List<Widget> template) {
    throw UnimplementedError(
      'startcustomprinting (List<Widget> template) has not been implemented.',
    );
  }
}
