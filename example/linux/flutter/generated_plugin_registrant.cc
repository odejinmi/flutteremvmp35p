//
//  Generated file. Do not edit.
//

// clang-format off

#include "generated_plugin_registrant.h"

#include <flutteremv/flutteremv_plugin.h>

void fl_register_plugins(FlPluginRegistry* registry) {
  g_autoptr(FlPluginRegistrar) flutteremv_registrar =
      fl_plugin_registry_get_registrar_for_plugin(registry, "FlutteremvPlugin");
  flutteremv_plugin_register_with_registrar(flutteremv_registrar);
}
