# flutteremv

A Plugin used to interfering Topwise Mp35p pos device.

## Getting Started

## How to use
This plugin exposes two APIs:

### 1. Initialize

Initialize the plugin. This should be done once.

``` dart
import 'package:flutteremv/flutteremv.dart';

class _MyAppState extends State<MyApp> {
final _flutteremvPlugin = Flutteremv();

  @override
  void initState() {
    _flutteremvPlugin.initialize("masterKey", "pinkey");
    super.initState();
  }
}
```

### 2. Initialize Payment

Create an object of the Transaction class and pass it to the initializePayment function

``` dart
Future<void> initPayment() async {
    _flutteremvPlugin.debitcard("2000");
}
```

### 3. Listen to card state

listening to card event to know the state of your card you can do whatever you like with the state

``` dart
 _flutteremvPlugin.stateStream.listen((values)  async {
      print(" card state $values");
      // Handle the state change here
      switch (values["state"]) {
        case "Loading":
          showDialog(context: context, builder: (builder)=> AlertDialog(title: Text("Loading"),));
          return ;
        case "CardData":
          eventresult = values;
          return ;
        case "CardReadTimeOut":
          return ;
        case "CallBackError":
          return ;
        case "CallBackCanceled":
          return ;
        case "CallBackTransResult":
          return ;
        case "CardDetected":
          var result = await Navigator.push(
            context,
            MaterialPageRoute(builder: (context) => Carpin(amount: "200",)),
          );
          if(result != null){
            _flutteremvPlugin.enterpin(result);
          }
          return ;

      }
    });
```

### 4. customise card pin layout

send pin entered back to the sdk card wait for response from the event you listen to earlier

``` dart
 _flutteremvPlugin.enterpin(result);
```

### 4. Withdraw receipt printing

Create an object of the Print class and pass it to the startprinting function

``` dart
                var args = Print(
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
                    bottommessage: "Buy Airtime and Pay Electricity bills here anytime!    AnyDAY!",
                  );
                  _topwisePlugin.startprinting(args).then((value) {print(value);});
```

### 6. Transfer receipt printing

Create an object of the Print class and pass it to the startprinting function

``` dart
                var args = Print(
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
                    bottommessage: "Buy Airtime and Pay Electricity bills here anytime!    AnyDAY!",
                  );
                  _topwisePlugin.startprinting(args).then((value) {print(value);});
```

### 7. Customise printing

when creating your ui for customise printing in consider the following
## 1. You can't use more than two widget in a row
## 2. The sdk understand limited Widget which are Column, Row, container, Expanded, Text, Image, Divider
## 3. if you are printing a list in the Ui, do not use ListView, you can use for loop
## 4. Any widget used differently from the above listed, sdk will interpret it has space


The TransactionMonitor class received after sdk is closed contains the below fields

```dart
String state;
String message;
bool status;
DebitCardRequestDto? transactionData;
```

## Need more information?
For further info about topwise's mobile SDKs, including setup, contact
odejinmiabraham@gmail.com

if you feel like contributing to this sdk kindly do so and notify so i can merge and publish
# topwise# topwise
