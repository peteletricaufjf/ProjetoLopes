/**
 * Arduino "Middleware". 
 * Conecta o programa (computador) ao mundo externo.
 * Funciona apenas como uma "ponte" do computador ao mundo externo
 */

bool DEBUG = true;

int SERIAL_BAUD = 9600;
int baud = 20; // Receber dados quantas vezes por segundo

// -- Comandos digitais [2,3,4,5]
// Mover para frente -> 2;
// Mover para trás ---> 3;
// Parar -------------> 4;
// Liberar bolas -----> 5;
// -- Comandos "analógicos" [6,7,8,9]
// Velocidade --------> De 0 a 15;
int pinsFromController[] = {2, 3, 4, 5, 6, 7, 8, 9};
int startOfAnalogPins = 4; // 4o pino é o início da velocidade

// -- Valores dos sensores
// Sensor 1 ----------> 10;
// Sensor 2 ----------> 11;
// Sensor 3 ----------> 12;
// Sensor de bolas ---> 13;
int pinsToController[] = {10, 11, 12, 13};

// "Calcula" o número de pinos usados
int numberOfpinsFromController = (sizeof(pinsFromController)/sizeof(int));
int numberOfpinsToController = (sizeof(pinsToController)/sizeof(int));

void setup() {
  Serial.begin(SERIAL_BAUD);

  // Inicializa os pinos de saída (indo para o "controller")
  for (int i = 0; i < numberOfpinsToController; i++) {
    pinMode(pinsToController[i], OUTPUT);
    digitalWrite(pinsToController[i], LOW);
  }

  // Inicializa os pinos de entrada (vindos do "controller")
  for (int i = 0; i < numberOfpinsFromController; i++) {
    pinMode(pinsFromController[i], INPUT);
  }
}

void loop() {
   
  // Lê os valores da Serial (enviados pelo computador) e transforma em valores digitais
  while (Serial.available()) {
    String buffer = Serial.readStringUntil('\r');
    if (buffer.length() != 0) {
      Serial.println(buffer[0]);
      switch (buffer[0]) {
        case 'T': configureBaud(buffer); break;
        case 'A': transmitSerialToDigitalPorts(buffer); break;
      }    
    }
  }

  // Lê os valores digitais enviados pelo "Controller" e envia para o computador em formato Serial
  transmitDigitalPortsToSerial();

  delay(1000/baud);
}

// Configura o baud pelo computador
void configureBaud(String buffer) {
    baud = buffer.substring(1).toInt();
    Serial.println("Received baud");
}

// Transforma os valores enviados por string pela Serial em valores digitais
void transmitSerialToDigitalPorts(String buffer) {
  String digital = buffer.substring(1);

  for (int i = 0; i < numberOfpinsToController; i++) {
    digitalWrite(pinsToController[i], digital.charAt(i) == '1');
  }
}

// Transforma os valores digitais em código para ser enviados por Serial para o computador
void transmitDigitalPortsToSerial() {
  String strToSend = "D";

  for (int i = 0; i < numberOfpinsFromController; i++) {
    if (i == startOfAnalogPins) { strToSend += "A"; }
    
    strToSend += digitalRead(pinsFromController[i]) == HIGH ? 1 : 0;
  }

  Serial.println(strToSend);
  Serial.flush();
}

