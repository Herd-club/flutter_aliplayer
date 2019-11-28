import 'dart:async';

import 'package:flutter/services.dart';

class FlutterAliplayer {
  static const MethodChannel _channel =
      const MethodChannel('flutter_aliplayer');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<String> play() async {
    print('hehhehehe');
    final String version = await _channel.invokeMethod('play');
    print('version ==${version}');
    return version;
  }
}
