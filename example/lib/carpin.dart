import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutteremv/flutteremv.dart';

class Carpin extends StatefulWidget {
  final String amount;
  const Carpin({Key? key, required this.amount}) : super(key: key);

  @override
  _CarpinState createState() => _CarpinState();
}

class _CarpinState extends State<Carpin> {
  int position = 0;
  String amountController = "";
  final _topwisemp35pPlugin = Flutteremv();

  Timer? _timer;
  @override
  void initState() {
    // TODO: implement initState
    super.initState();
    _topwisemp35pPlugin.startkeyboard(
      onchange: result,
      proceed: proceed,
      cancel: cancel,
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Enter card pin')),
      body: Container(
        color: Colors.white,
        child: Column(
          children: [
            const SizedBox(height: 20),
            const Row(),
            if (position == 0)
              Column(
                children: [
                  Container(
                    height: 45,
                    padding: const EdgeInsets.symmetric(
                      horizontal: 24,
                      vertical: 4,
                    ),
                    decoration: ShapeDecoration(
                      color: const Color(0xFFF7F9FC),
                      shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(4),
                      ),
                    ),
                    child: Row(
                      mainAxisSize: MainAxisSize.min,
                      mainAxisAlignment: MainAxisAlignment.start,
                      crossAxisAlignment: CrossAxisAlignment.center,
                      children: [
                        Text(
                          '₦',
                          style: TextStyle(
                            color: Color(0xFF19191F),
                            fontSize: 15,
                            fontFamily: 'Inter',
                            fontWeight: FontWeight.w600,
                          ),
                        ),
                        Text(
                          widget.amount,
                          style: const TextStyle(
                            color: Color(0xFF19191F),
                            fontSize: 24,
                            fontFamily: 'Inter',
                            fontWeight: FontWeight.w600,
                            height: 0,
                          ),
                        ),
                        const SizedBox(width: 3.02),
                        const Text(
                          '.00',
                          style: TextStyle(
                            color: Color(0xFF5A667A),
                            fontSize: 14,
                            fontFamily: 'Inter',
                            fontWeight: FontWeight.w600,
                            height: 0,
                          ),
                        ),
                      ],
                    ),
                  ),
                  const SizedBox(height: 10),
                  const Text(
                    'Enter 4-digit Card PIN',
                    style: TextStyle(
                      color: Color(0xFF5A667A),
                      fontSize: 12,
                      fontFamily: 'Inter',
                      fontWeight: FontWeight.w500,
                      height: 0,
                    ),
                  ),
                  const SizedBox(height: 20),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.center,
                    crossAxisAlignment: CrossAxisAlignment.center,
                    children: [
                      Container(
                        width: 14.14,
                        height: 14.14,
                        decoration: ShapeDecoration(
                          color: buttoncolor(1),
                          shape: RoundedRectangleBorder(
                            borderRadius: BorderRadius.circular(7.07),
                          ),
                        ),
                      ),
                      const SizedBox(width: 16),
                      Container(
                        width: 14.14,
                        height: 14.14,
                        decoration: ShapeDecoration(
                          color: buttoncolor(2),
                          shape: RoundedRectangleBorder(
                            borderRadius: BorderRadius.circular(7.07),
                          ),
                        ),
                      ),
                      const SizedBox(width: 16),
                      Container(
                        width: 14.14,
                        height: 14.14,
                        decoration: ShapeDecoration(
                          color: buttoncolor(3),
                          shape: RoundedRectangleBorder(
                            borderRadius: BorderRadius.circular(7.07),
                          ),
                        ),
                      ),
                      const SizedBox(width: 16),
                      Container(
                        width: 14.14,
                        height: 14.14,
                        decoration: ShapeDecoration(
                          color: buttoncolor(4),
                          shape: RoundedRectangleBorder(
                            borderRadius: BorderRadius.circular(7.07),
                          ),
                        ),
                      ),
                    ],
                  ),
                ],
              ),
            if (position >= 1)
              Column(
                mainAxisSize: MainAxisSize.min,
                mainAxisAlignment: MainAxisAlignment.start,
                crossAxisAlignment: CrossAxisAlignment.center,
                children: [
                  Text(
                    position == 1 ? 'Validating...' : 'Processing transaction',
                    textAlign: TextAlign.center,
                    style: const TextStyle(
                      color: Color(0xFF5A667A),
                      fontSize: 12,
                      fontFamily: 'Inter',
                      fontWeight: FontWeight.w500,
                    ),
                  ),
                ],
              ),
          ],
        ),
      ),
    );
  }

  Color buttoncolor(int position) {
    return amountController.length >= position
        ? Color(0xFF34AA44)
        : Color(0xFFB9C2D2);
  }

  void result(String value) {
    if (value != "delete") {
      if (amountController.toString().length < 4) {
        amountController += value;
      }
    } else {
      if (amountController.isNotEmpty &&
          amountController != "0" &&
          amountController.toString().length > 1) {
        amountController = amountController.substring(
          0,
          amountController.length - 1,
        );
      } else {
        amountController = '';
      }
    }
    setState(() {});
  }

  @override
  void dispose() {
    _topwisemp35pPlugin.stopkeyboard();
    super.dispose();
  }

  proceed() {
    print("return back to withdraw");
    Navigator.pop(context, amountController);
    _topwisemp35pPlugin.stopkeyboard();
  }

  cancel() {
    _topwisemp35pPlugin.cancelcardprocess();
    Navigator.pop(context);
  }
}
