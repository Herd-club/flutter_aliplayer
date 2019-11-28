#import "FlutterAliplayerPlugin.h"
#import <flutter_aliplayer/flutter_aliplayer-Swift.h>

@implementation FlutterAliplayerPlugin {
    UIViewController *_viewController;
}
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftFlutterAliplayerPlugin registerWithRegistrar:registrar];
}

@end
