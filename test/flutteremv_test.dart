import 'package:flutter_test/flutter_test.dart';
import 'package:flutteremv/flutteremv.dart';
import 'package:flutteremv/flutteremv_platform_interface.dart';
import 'package:flutteremv/flutteremv_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockFlutteremvPlatform
    with MockPlatformInterfaceMixin
    implements FlutteremvPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final FlutteremvPlatform initialPlatform = FlutteremvPlatform.instance;

  test('$MethodChannelFlutteremv is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelFlutteremv>());
  });

  test('getPlatformVersion', () async {
    Flutteremv flutteremvPlugin = Flutteremv();
    MockFlutteremvPlatform fakePlatform = MockFlutteremvPlatform();
    FlutteremvPlatform.instance = fakePlatform;

    expect(await flutteremvPlugin.getPlatformVersion(), '42');
  });
}
