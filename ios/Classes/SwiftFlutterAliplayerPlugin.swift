import Flutter
import UIKit

public class SwiftFlutterAliplayerPlugin: NSObject, FlutterPlugin {
  
  public static func register(with registrar: FlutterPluginRegistrar) {
    let channel = FlutterMethodChannel(name: "flutter_aliplayer", binaryMessenger: registrar.messenger())
    let instance = SwiftFlutterAliplayerPlugin()
    registrar.addMethodCallDelegate(instance, channel: channel)
  }

  public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
    switch call.method {
        case "play":
            handlePlay(result: result)
        default:
            break;

    }
  }

  public func handlePlay(result: @escaping FlutterResult) {
    let rootViewController: UIViewController? = UIApplication.shared.keyWindow?.rootViewController
    rootViewController!.present(PlayerViewController(), animated: true, completion: nil)
    result(nil)
    rootViewController!.dismiss(animated: true, completion: nil)
  }


}
