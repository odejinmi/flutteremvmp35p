#include "include/flutteremv/flutteremv_plugin_c_api.h"

#include <flutter/plugin_registrar_windows.h>

#include "flutteremv_plugin.h"

void FlutteremvPluginCApiRegisterWithRegistrar(
    FlutterDesktopPluginRegistrarRef registrar) {
  flutteremv::FlutteremvPlugin::RegisterWithRegistrar(
      flutter::PluginRegistrarManager::GetInstance()
          ->GetRegistrar<flutter::PluginRegistrarWindows>(registrar));
}
