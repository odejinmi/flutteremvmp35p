import 'dart:async';
import 'dart:convert';
import 'dart:math';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutteremv/flutteremv.dart';
import 'package:flutteremv/print.dart';
import 'package:flutteremv_example/receipt.dart';
import 'package:pointycastle/api.dart';
import 'package:pointycastle/digests/sha1.dart';
import 'package:pointycastle/key_derivators/api.dart';
import 'package:pointycastle/key_derivators/pbkdf2.dart';
import 'package:pointycastle/macs/hmac.dart';

import 'carpin.dart';
import 'eod.dart';

class Home extends StatefulWidget {
  const Home({Key? key}) : super(key: key);

  @override
  _HomeState createState() => _HomeState();
}

class _HomeState extends State<Home> {
  String _platformVersion = 'Unknown';
  final _topwisemp35pPlugin = Flutteremv();

  var eventresult = {};

  String amount = "500";

  @override
  void initState() {
    super.initState();
    initPlatformState();
    _topwisemp35pPlugin.stateStream.listen((values) async {
      print(" card state $values");
      // Handle the state change here
      switch (values["state"]) {
        case "Loading":
          showDialog(
            context: context,
            builder: (builder) => AlertDialog(title: Text("Loading")),
          );
          return;
        case "CardData":
          eventresult = values;
          return;
        case "CardReadTimeOut":
          return;
        case "CallBackError":
          return;
        case "CallBackCanceled":
          return;
        case "CallBackTransResult":
          return;
        case "CardDetected":
          Navigator.pop(context);
          var result = await Navigator.push(
            context,
            MaterialPageRoute(builder: (context) => Carpin(amount: amount)),
          );
          if (result != null) {
            _topwisemp35pPlugin.enterpin(result);
          }
          return;
      }
    });
    start();
  }

  late String base64string;

  Future<void> start() async {
    final ByteData assetByteData = await rootBundle.load("asset/logo2.png");
    final Uint8List imagebytes = assetByteData.buffer.asUint8List();
    base64string = base64.encode(imagebytes); //convert bytes to base64 string
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    // We also handle the message potentially returning null.
    try {
      var result = await _topwisemp35pPlugin.initialize(
        "3F2216D8297BCE9C",
        "0000000002DDDDE00001",
      );
      print(result);
      platformVersion =
          await _topwisemp35pPlugin.deviceserialnumber() ??
          'Unknown platform version';
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Plugin example app')),
      body: SingleChildScrollView(
        child: Column(
          children: [
            Center(child: Text('Running on: $_platformVersion\n')),
            ElevatedButton(
              onPressed: () async {
                _topwisemp35pPlugin.debitcard(amount);
              },
              child: const Text("start transaction"),
            ),
            ElevatedButton(
              onPressed: () async {
                var result = await _topwisemp35pPlugin.getcardsheme("200");
                print("tolu result yuuiouiyr");
                print(result);
                print("tolu result gotting");
              },
              child: const Text("get cardsheme"),
            ),
            ElevatedButton(
              onPressed: () {
                var args = Print(
                  base64image: base64string,
                  marchantname: "VERDANT MICROFINANCE BANK",
                  datetime: "27 Jan 2023,06:55AM",
                  terminalid: "2LUX4199",
                  merchantid: "2LUXAA00000001",
                  transactiontype: "CARD WITHDRAWAL",
                  copytype: "Merchant",
                  rrn: "561409897476",
                  stan: "904165",
                  pan: "539983******1954",
                  expiry: "2303",
                  transactionstatus: "DECLINED",
                  responsecode: "55",
                  message: "Incorrect PIN",
                  appversion: "1.5.3",
                  amount: "200",
                  bottommessage:
                      "Buy Airtime and Pay Electricity bills here anytime!    AnyDAY!",
                  marchantaddress: '',
                  serialno: '',
                );
                _topwisemp35pPlugin.startprinting(args).then((value) {
                  print(value);
                });
              },
              child: const Text("print withdraw"),
            ),
            ElevatedButton(
              onPressed: () {
                var args = Print(
                  rrn: "gfhj",
                  pan: "fsdgs",
                  expiry: "fdfs",
                  base64image: base64string,
                  marchantname: "VERDANT MICROFINANCE BANK",
                  datetime: "27 Jan 2023,06:55AM",
                  terminalid: "2LUX4199",
                  merchantid: "2LUXAA00000001",
                  transactiontype: "CARD WITHDRAWAL",
                  accountname: "ODEJINMI TOLUWALOPE ABRAHAM",
                  copytype: "Merchant",
                  stan: "904165",
                  accountnumber: "3076302098",
                  bank: "First Bank",
                  transactionstatus: "DECLINED",
                  responsecode: "55",
                  message: "Incorrect PIN",
                  appversion: "1.5.3",
                  amount: "200",
                  bottommessage:
                      "Buy Airtime and Pay Electricity bills here anytime!    AnyDAY!",
                  marchantaddress: '',
                  serialno: '',
                );
                _topwisemp35pPlugin.startprinting(args).then((value) {
                  print(value);
                });
              },
              child: const Text("print transfer"),
            ),
            ElevatedButton(
              onPressed: () async {
                // var printvalue = [
                //   {"image":base64string,"align":"center","imagewidth":30, "imageheight":30},
                //   {"text":[{"text":"MERCHANT NAME", "textsize":"normal","align":"center"}]},
                //   {"text":[{"text":"Tolulope", "textsize":"normal","align":"center"}]},
                //   {"text":[{"text":"DATE/TIME", "textsize":"normal","align":"left"},{"text":"tolulope", "textsize":"normal","align":"right"}]},
                //   {"text":[{"text":"TERMINAL ID", "textsize":"normal","align":"left"},{"text":"tolulope", "textsize":"normal","align":"right"}]},
                //   {"text":[{"text":"MERCHANT ID", "textsize":"normal","align":"left"},{"text":"tolulope", "textsize":"normal","align":"right"}]},
                //   {"text":[{"text":"", "textsize":"normal","align":"center"}]},
                //   {"text":[{"text":"*****************************************************"}]},
                //   {"text":[{"text":"transactiontype", "textsize":"normal","align":"center"}]},
                //   {"text":[{"text":"accountname", "textsize":"normal","align":"center"}]},
                //   {"text":[{"text":"Customer Copy", "textsize":"normal","align":"center"}]},
                //   {"text":[{"text":"*****************************************************"}]},
                //   {"text":[{"text":"RRN:", "textsize":"normal","align":"left"},{"text":"tolulope", "textsize":"normal","align":"right"}]},
                //   {"text":[{"text":"STAN:", "textsize":"normal","align":"left"},{"text":"tolulope", "textsize":"normal","align":"right"}]},
                //   {"text":[{"text":"PAN:", "textsize":"normal","align":"left"},{"text":"tolulope", "textsize":"normal","align":"right"}]},
                //   {"text":[{"text":"CARD EXPIRY", "textsize":"normal","align":"left"},{"text":"tolulope", "textsize":"normal","align":"right"}]},
                //   {"text":[{"text":"ACCOUNT NUMBER:", "textsize":"normal","align":"left"},{"text":"tolulope", "textsize":"normal","align":"right"}]},
                //   {"text":[{"text":"BANK", "textsize":"normal","align":"left"},{"text":"tolulope", "textsize":"normal","align":"right"}]},
                //   {"text":[{"text":"transactionstatus", "textsize":"normal","align":"center"}]},
                //   {"text":[{"text":"Response code", "textsize":"normal","align":"left"},{"text":"tolulope", "textsize":"normal","align":"right"}]},
                //   {"text":[{"text":"Message", "textsize":"normal","align":"left"},{"text":"tolulope", "textsize":"normal","align":"right"}]},
                //   {"text":[{"text":"App Version", "textsize":"normal","align":"left"},{"text":"tolulope", "textsize":"normal","align":"right"}]},
                //   {"text":[{"text":"", "textsize":"normal","align":"center"}]},
                //   {"text":[{"text":"*****************************************************"}]},
                //   {"text":[{"text":"₦2000.00", "textsize":"normal","align":"center"}]},
                //   {"text":[{"text":"*****************************************************"}]},
                //   {"text":[{"text":"bottommessage", "textsize":"normal","align":"center"}]},
                //   {"text":[{"text":"*****************************************************"}]},
                //   {"text":[{"text":"", "textsize":"normal","align":"center"}]},
                //   {"text":[{"text":"", "textsize":"normal","align":"center"}]},
                //   {"text":[{"text":"", "textsize":"normal","align":"center"}]},
                // ];
                // final List<Printmodel> printmodel = printmodelFromJson(jsonEncode(printvalue));
                // _topwisemp35pPlugin.startcustomprinting(printmodel).then((value) {print(value);});

                Navigator.push(
                  context,
                  MaterialPageRoute(builder: (context) => Receipt()),
                );
              },
              child: const Text("custom printing"),
            ),
            ElevatedButton(
              onPressed: () async {
                Navigator.push(
                  context,
                  MaterialPageRoute(builder: (context) => const Eod()),
                );
              },
              child: const Text("Eod printing"),
            ),
          ],
        ),
      ),
    );
  }
}

class Encrypt {
  static const int _keySize = 256;
  static const int _derivationIterations = 1000;
  static const String _algorithm = "AES";
  static const int _saltSize = 32; // 256 bits (not 128 as in comment)
  static const int _ivSize = 32; // 256 bits (not 128 as in comment)

  // You'll need to replace these with your actual values from PosApplication
  static String get _ipeklive =>
      "your_password_here"; // Replace with actual password
  static String get _ksnlive =>
      "PBKDF2WithHmacSHA1"; // Replace with actual algorithm

  /// Encrypts plaintext using AES encryption with PBKDF2 key derivation
  /// [plainText] The text to encrypt
  /// Returns Base64 encoded string containing salt + IV + encrypted data
  static String encrypt(String plainText) {
    try {
      // Generate random salt and IV
      final salt = _generate256BitsOfRandomEntropy();
      final iv =
          _generate256BitsOfRandomEntropy(); // IV should be 128 bits for AES

      // Convert plaintext to bytes
      final plainTextBytes = utf8.encode(plainText);

      // Derive key from password using PBKDF2
      final key = _deriveKey(_ipeklive, salt);

      // Initialize cipher for encryption
      final cipher = PaddedBlockCipher('AES/CBC/PKCS7');
      final params =
          PaddedBlockCipherParameters<CipherParameters, CipherParameters>(
            ParametersWithIV<KeyParameter>(KeyParameter(key), iv),
            null,
          );

      cipher.init(true, params); // true for encryption

      // Encrypt the plaintext
      final encryptedBytes = cipher.process(Uint8List.fromList(plainTextBytes));

      // Concatenate salt + IV + encrypted data
      final result = Uint8List(salt.length + iv.length + encryptedBytes.length);
      result.setRange(0, salt.length, salt);
      result.setRange(salt.length, salt.length + iv.length, iv);
      result.setRange(salt.length + iv.length, result.length, encryptedBytes);

      // Return Base64 encoded result
      return base64.encode(result);
    } catch (e) {
      throw Exception('Encryption failed: $e');
    }
  }

  /// Decrypts ciphertext that was encrypted using the encrypt method
  /// [cipherText] Base64 encoded string containing salt + IV + encrypted data
  /// [password] The password used for encryption
  /// Returns the decrypted plaintext
  static String decrypt(String cipherText, String password) {
    try {
      // Decode Base64 ciphertext
      final cipherTextBytesWithSaltAndIv = base64.decode(cipherText);

      // Extract salt (first 32 bytes)
      final salt = Uint8List(_saltSize);
      salt.setRange(0, _saltSize, cipherTextBytesWithSaltAndIv);

      // Extract IV (next 16 bytes) - IV for AES is always 128 bits
      final iv = Uint8List(16);
      iv.setRange(0, 16, cipherTextBytesWithSaltAndIv.skip(_saltSize));

      // Extract encrypted data (remaining bytes)
      final encryptedBytes = Uint8List(
        cipherTextBytesWithSaltAndIv.length - _saltSize - 16,
      );
      encryptedBytes.setRange(
        0,
        encryptedBytes.length,
        cipherTextBytesWithSaltAndIv.skip(_saltSize + 16),
      );

      // Derive key from password using same parameters as encryption
      final key = _deriveKey(password, salt);

      // Initialize cipher for decryption
      final cipher = PaddedBlockCipher('AES/CBC/PKCS7');
      final params =
          PaddedBlockCipherParameters<CipherParameters, CipherParameters>(
            ParametersWithIV<KeyParameter>(KeyParameter(key), iv),
            null,
          );

      cipher.init(false, params); // false for decryption

      // Decrypt the data
      final decryptedBytes = cipher.process(encryptedBytes);

      // Convert back to string
      return utf8.decode(decryptedBytes);
    } catch (e) {
      throw Exception('Decryption failed: $e');
    }
  }

  /// Derives a key from password and salt using PBKDF2
  static Uint8List _deriveKey(String password, Uint8List salt) {
    final pbkdf2 = PBKDF2KeyDerivator(HMac(SHA1Digest(), 64));
    pbkdf2.init(Pbkdf2Parameters(salt, _derivationIterations, _keySize ~/ 8));
    return pbkdf2.process(utf8.encode(password));
  }

  /// Generates 256 bits (32 bytes) of cryptographically secure random data
  static Uint8List _generate256BitsOfRandomEntropy() {
    final random = Random.secure();
    final bytes = Uint8List(32);
    for (int i = 0; i < 32; i++) {
      bytes[i] = random.nextInt(256);
    }
    return bytes;
  }

  /// Generates 128 bits (16 bytes) of cryptographically secure random data for IV
  static Uint8List _generate128BitsOfRandomEntropy() {
    final random = Random.secure();
    final bytes = Uint8List(16);
    for (int i = 0; i < 16; i++) {
      bytes[i] = random.nextInt(256);
    }
    return bytes;
  }
}
