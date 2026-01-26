// // const char* ssid = "147-4F";
// // const char* password = "00000000";
// // const char* serverUrl = "http://192.168.1.104:9090/circuitData";
// #include <PZEM004Tv30.h>

// PZEM004Tv30 pzem(11, 12); // Software Serial pin 11 (RX) & 12 (TX)

// void setup() {
//    Serial.begin(9600);
// }

// void loop() {
//    float voltage = pzem.voltage();
//    if(voltage != NAN){
//        Serial.print("Voltage: ");
//        Serial.print(voltage);
//        Serial.println("V");
//    } else {
//        Serial.println("Error reading voltage");
//    }

//    float current = pzem.current();
//    if(current != NAN){
//        Serial.print("Current: ");
//        Serial.print(current);
//        Serial.println("A");
//    } else {
//        Serial.println("Error reading current");
//    }

//    float power = pzem.power();
//    if(current != NAN){
//        Serial.print("Power: ");
//        Serial.print(power);
//        Serial.println("W");
//    } else {
//        Serial.println("Error reading power");
//    }

//    float energy = pzem.energy();
//    if(current != NAN){
//        Serial.print("Energy: ");
//        Serial.print(energy,3);
//        Serial.println("kWh");
//    } else {
//        Serial.println("Error reading energy");
//    }

//    float frequency = pzem.frequency();
//    if(current != NAN){
//        Serial.print("Frequency: ");
//        Serial.print(frequency, 1);
//        Serial.println("Hz");
//    } else {
//        Serial.println("Error reading frequency");
//    }

//    float pf = pzem.pf();
//    if(current != NAN){
//        Serial.print("PF: ");
//        Serial.println(pf);
//    } else {
//        Serial.println("Error reading power factor");
//    }

//    Serial.println();
//    delay(2000);
// }






void setup() {
  Serial.begin(9600);   // 這就是 D1(TX) / D0(RX)
}

void loop() {
  // 隨便送資料，看 PZEM RX 燈會不會亮
  Serial.write(0xB4);
  delay(1000);
}
