import 'package:flutter/services.dart';

class Keyevent {
  // late TextEditingController amountController;
  final ValueChanged<String>? onchange;
  // late BuildContext context;

  Function? proceed, cancel;

  Keyevent({
    // required this.amountController,
    this.onchange,
    this.cancel,
    this.proceed,
  });

  bool handleKeyEvent(KeyEvent event) {
    print(event);
    String key = '';
    if (event is KeyDownEvent) {
      if (event.logicalKey.keyId >= LogicalKeyboardKey.digit1.keyId &&
          event.logicalKey.keyId <= LogicalKeyboardKey.digit9.keyId) {
        key =
            (event.logicalKey.keyId - LogicalKeyboardKey.digit1.keyId + 1)
                .toString();
      } else if (event.logicalKey.keyId == LogicalKeyboardKey.digit0.keyId) {
        key = '0';
      } else if (event.logicalKey.keyId == LogicalKeyboardKey.enter.keyId) {
        if (proceed != null) {
          proceed!();
        }
      } else if (event.logicalKey.keyId == LogicalKeyboardKey.delete.keyId ||
          event.logicalKey.keyId == LogicalKeyboardKey.backspace.keyId) {
        // if (key.isNotEmpty) {
        //   key = key.substring(
        //       0, key.length - 1);
        // }
        key = "delete";
      } else if (event.logicalKey.keyId == LogicalKeyboardKey.goBack.keyId ||
          event.logicalKey.keyId == LogicalKeyboardKey.escape.keyId) {
        if (cancel != null) {
          cancel!();
          // }else{
          //   Get.back();
        }
      }
      // else if (// Add logic for other valid symbols here) {
      // // Allow other relevant symbols (e.g., '.')
      // key = getSymbolFromKeyCode(event.logicalKey.keyId);
    } else {
      // Handle invalid input (show a toast or snackbar)
      // print('Invalid input: ${event.logicalKey}');
    }

    if (key.isNotEmpty && onchange != null) {
      // amountController.text += key;
      onchange!(key);
    }
    return true;
  }

  bool handleKeyEventstream(dynamic value) {
    value.listen((event) async {
      print("dynamic key event result");
      print(event);
      String key = '';

      if (event["state"] == "inputkey") {
        onchange!(event["message"]);
      } else if (event["state"] == "confirm") {
        if (proceed != null) {
          proceed!();
        }
      } else if (event["state"] == "cancel") {
        if (cancel != null) {
          cancel!();
          // }else{
          //   Get.back();
        }
      } else if (event["state"] == "stop") {
        onchange!(event["message"]);
      } else {}
      if (event is KeyDownEvent) {
        if (event.logicalKey.keyId >= LogicalKeyboardKey.digit1.keyId &&
            event.logicalKey.keyId <= LogicalKeyboardKey.digit9.keyId) {
          key =
              (event.logicalKey.keyId - LogicalKeyboardKey.digit1.keyId + 1)
                  .toString();
        } else if (event.logicalKey.keyId == LogicalKeyboardKey.digit0.keyId) {
          key = '0';
        } else if (event.logicalKey.keyId == LogicalKeyboardKey.enter.keyId) {
          if (proceed != null) {
            proceed!();
          }
        } else if (event.logicalKey.keyId == LogicalKeyboardKey.delete.keyId ||
            event.logicalKey.keyId == LogicalKeyboardKey.backspace.keyId) {
          // if (key.isNotEmpty) {
          //   key = key.substring(
          //       0, key.length - 1);
          // }
          key = "delete";
        } else if (event.logicalKey.keyId == LogicalKeyboardKey.goBack.keyId ||
            event.logicalKey.keyId == LogicalKeyboardKey.escape.keyId) {
          if (cancel != null) {
            cancel!();
            // }else{
            //   Get.back();
          }
        }
        // else if (// Add logic for other valid symbols here) {
        // // Allow other relevant symbols (e.g., '.')
        // key = getSymbolFromKeyCode(event.logicalKey.keyId);
      } else {
        // Handle invalid input (show a toast or snackbar)
        // print('Invalid input: ${event.logicalKey}');
      }

      if (key.isNotEmpty && onchange != null) {
        // amountController.text += key;
        onchange!(key);
      }
    });
    return true;
  }
}
