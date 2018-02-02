/**
 * Arduino "controller".
 * Se conecta ao Arduino "Middleware" e serve como controlador.
 */

bool DEBUG = true;

int SERIAL_BAUD = 9600;
int baud = 20; // Receber dados quantas vezes por segundo

// -- Valores dos sensores
// Sensor 1 ----------> 10;
// Sensor 2 ----------> 11;
// Sensor 3 ----------> 12;
// Sensor de bolas ---> 13;
int pinsFromMiddleware[] = {10, 11, 12, 13};
int sensor1 = pinsFromMiddleware[0];
int sensor2 = pinsFromMiddleware[1];
int sensor3 = pinsFromMiddleware[2];
int sensorDrop = pinsFromMiddleware[3];

// -- Comandos digitais [2,3,4,5]
// Mover para frente -> 2;
// Mover para trás ---> 3;
// Parar -------------> 4;
// Liberar bolas -----> 5;
//
// -- Comandos analógicos [6,7,8,9]
// Velocidade --------> De 0 a 9999;
int pinsToMiddleware[] = {2, 3, 4, 5, 6, 7, 8, 9};
int moveForwardPin = pinsToMiddleware[0];
int moveBackwardsPin = pinsToMiddleware[1];
int stopPin = pinsToMiddleware[2];
int dropBallPin = pinsToMiddleware[3];

// "Calcula" o número de pinos usados
int numberOfpinsFromMiddleware = (sizeof(pinsFromMiddleware)/sizeof(int));
int numberOfpinsToMiddleware = (sizeof(pinsToMiddleware)/sizeof(int));

// Variáveis de suporte
int ballsDroped = 0;
int lastSensorDropValue = 0;
bool shouldDrop = true;

void setup() {
  Serial.begin(SERIAL_BAUD);
  
  // Inicializa os pinos de saída (indo para o "middleware")
  for (int i = 0; i < (sizeof(pinsToMiddleware)/sizeof(int)); i++) {
    pinMode(pinsToMiddleware[i], OUTPUT);
    digitalWrite(pinsToMiddleware[i], LOW);
    setSpeed(15); // Inicia com velocidade máxima
  }

  // Inicializa os pinos de entrada (vindos do "middleware")
  for (int i = 0; i < (sizeof(pinsFromMiddleware)/sizeof(int)); i++) {
    pinMode(pinsFromMiddleware[i], INPUT);
  }
}

void loop() {
  // Faz toda a lógica e envia de volta
  analyseAndReply();

  if(DEBUG){
//    Serial.println("Valores recebidos do Middleware: ");
//    Serial.println("");
//    
//    Serial.print("Sensor 1: ");
//    Serial.println(digitalRead(sensor1));
//    Serial.print("Sensor 2: ");
//    Serial.println(digitalRead(sensor2));
//    Serial.print("Sensor 3: ");
//    Serial.println(digitalRead(sensor3));
//    Serial.print("Sensor Drop: ");
//    Serial.println(digitalRead(sensorDrop));
//    Serial.print("Sensor analog: ");
//    Serial.print(digitalRead(pinsToMiddleware[4]));
//    Serial.print(digitalRead(pinsToMiddleware[5]));
//    Serial.print(digitalRead(pinsToMiddleware[6]));
//    Serial.println(digitalRead(pinsToMiddleware[7]));
//    Serial.println("");
  
//    Serial.print("Move forward: ");
//    Serial.println(digitalRead(moveForwardPin));
//    Serial.print("Move backwards: ");
//    Serial.println(digitalRead(moveBackwardsPin));
//    Serial.print("Stop: ");
//    Serial.println(digitalRead(stopPin));
//    Serial.print("Drop: ");
//    Serial.println(digitalRead(dropBallPin));
//    Serial.println("");
//    Serial.print("BallsDroped: ");
//    Serial.println(ballsDroped);
//    Serial.println("");
  }

  delay(1000 / baud);
}

/**
 * Função de controle de lógica.
 */
void analyseAndReply() {
   
  if (digitalRead(sensor1)) {
    setSpeed(8);
  }

  else if (digitalRead(sensor2)) {
    stop();

    if (ballsDroped >= 4) {
      Serial.println("ballDroped");
      ballsDroped = 0;
      shouldDrop = false;
    } 
       
    else if(shouldDrop) {
      stop();
      if (digitalRead(sensorDrop) && lastSensorDropValue == 0) {
        Serial.println("shouldDrop");
        ballsDroped += 1;
        lastSensorDropValue = 1;
      } else if (!digitalRead(sensorDrop)) {
        Serial.println("sensorDrop");
        lastSensorDropValue = 0;
      }
      dropBall();
    } 
    
    else {
     Serial.println("mvForw");

      moveForward();
    }

  }

  if (digitalRead(sensor3)) {
    stop();
  }

  else {
    moveForward();
  }
}

void send(int pin, boolean value) {
  digitalWrite(pin, value);
}

void setSpeed(int speed) {
  if (speed < 0 || speed > 15) return;

  int bitsCount = 4;
  while (bitsCount--){
    digitalWrite(pinsToMiddleware[7 - bitsCount], bitRead(speed, bitsCount));
  }
}

void stop() {
  send(moveForwardPin,   false);
  send(moveBackwardsPin, false);
  send(stopPin,          true );
}

void moveForward() {
  send(moveForwardPin,   true );
  send(moveBackwardsPin, false);
  send(stopPin,          false);
  send(dropBallPin,      false);
}

void moveBackwards() {
  send(moveForwardPin,   false);
  send(moveBackwardsPin, true );
  send(stopPin,          false);
  send(dropBallPin,      false);
}

void dropBall() {
  send(moveForwardPin,   false);
  send(moveBackwardsPin, false);
  send(dropBallPin,      true);
}

