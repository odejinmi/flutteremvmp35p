#ifndef FLUTTER_PLUGIN_FLUTTEREMV_PLUGIN_H_
#define FLUTTER_PLUGIN_FLUTTEREMV_PLUGIN_H_

#include <flutter/method_channel.h>
#include <flutter/plugin_registrar_windows.h>

#include <memory>

namespace flutteremv {

class FlutteremvPlugin : public flutter::Plugin {
 public:
  static void RegisterWithRegistrar(flutter::PluginRegistrarWindows *registrar);

  FlutteremvPlugin();

  virtual ~FlutteremvPlugin();

  // Disallow copy and assign.
  FlutteremvPlugin(const FlutteremvPlugin&) = delete;
  FlutteremvPlugin& operator=(const FlutteremvPlugin&) = delete;

  // Called when a method is called on this plugin's channel from Dart.
  void HandleMethodCall(
      const flutter::MethodCall<flutter::EncodableValue> &method_call,
      std::unique_ptr<flutter::MethodResult<flutter::EncodableValue>> result);
};

}  // namespace flutteremv

#endif  // FLUTTER_PLUGIN_FLUTTEREMV_PLUGIN_H_
