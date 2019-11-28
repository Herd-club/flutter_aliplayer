import Flutter
import UIKit

public class SwiftFlutterAliplayerPlugin: NSObject, FlutterPlugin {
  public var viewController: UIViewController?

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

  public func initViewController() {
    viewController = PlayerViewController()
    viewController!.view.backgroundColor = UIColor.systemGray
  }

  public func handlePlay(result: @escaping FlutterResult) {
    let rootViewController: UIViewController? = UIApplication.shared.keyWindow?.rootViewController
    initViewController()
    rootViewController!.present(viewController!, animated: true, completion: nil)
    result(nil)
    rootViewController!.dismiss(animated: true, completion: nil)
//     DispatchQueue.main.async {
//            let alert = UIAlertController(title: "Alert", message: "Hi, My name is flutter", preferredStyle: .alert);
//            alert.addAction(UIAlertAction(title: "OK", style: UIAlertActionStyle.default, handler: nil))
//            UIApplication.shared.keyWindow?.rootViewController?.present(alert, animated: true, completion: nil);
//        }

  }


}
