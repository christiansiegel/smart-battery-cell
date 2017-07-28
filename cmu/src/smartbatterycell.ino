/*
	Smart Battery Cell

	Created 2016-08-29
	Christian Siegel, Aarhus University
*/

#include <ESP8266WiFi.h>
#include <WiFiUdp.h>

///////////////////////////////////////////////////////////////////////////////
// Config
///////////////////////////////////////////////////////////////////////////////

const int PIN_ADC_BATTERY = A0;
const int PIN_ADC_GROUND = 4;
const int PIN_MOSFET = 5;

// Needs to be class C network.
//const char* WIFI_SSID = "Internet";
//const char* WIFI_PASSPHRASE = "sX989oDXcKCyTXaO";

const char* WIFI_SSID = "SmartCellGateway";
const char* WIFI_PASSPHRASE = "allyourcellsarebelongtome";

const uint16_t PORT = 4444;

// Time in ms after which cached cell states are dropped.
const uint16_t CELL_CACHE_MEMORY = 2000; // 2 seconds

// Interval after which the cell state is broadcasted
const uint16_t STATE_BROADCAST_INTERVAL = 500;

///////////////////////////////////////////////////////////////////////////////
// Enumeration Types
///////////////////////////////////////////////////////////////////////////////

// Message types used as message headers in order to distinguish between them.
typedef enum {
    // Message containing the state of a single cell.
    MESSAGE_STATE_UPDATE = 0,
    // Message to set the balancing mode of cells (see also balancingType_t).
    MESSAGE_BALANCING_CONTROL = 1,
} messageType_t;

// Balancing mode a cell can perform.
typedef enum {
    // Manual passive balancing (controlled by external messages).
    BALANCING_PASSIVE_MANUAL = 0,
    // Continuous auto passive balancing
    BALANCING_PASSIVE_AUTO = 1,
} balancingMode_t;

///////////////////////////////////////////////////////////////////////////////
// Structure types
///////////////////////////////////////////////////////////////////////////////

// Full state information of a cell.
typedef struct {
    // Voltage in mV.
    uint16_t voltage;
    // State of the passive balancing.
    bool isBalancing;
    // Active balancing mode.
    balancingMode_t balancingMode;
} cellState_t;

// Aggregated state information of the battery pack.
typedef struct {
    // Minimum cell voltage in the pack [mV].
    uint16_t voltageMin;
    // Maximum cell voltage in the pack [mV].
    uint16_t voltageMax;
    // Mean cell voltage in the pack [mV].
    uint16_t voltageMean;
    // Number of cells in the pack.
    uint8_t numberOfCells;
} aggregatedPackState_t;

// Reduced cell state information used to cache all cells and
// aggregate their values.
typedef struct {
    // Voltage in mV.
    uint16_t voltage;
    // Timestamp of last update in ms (see millis()).
    uint32_t lastUpdated;
} cellCacheEntity_t;

///////////////////////////////////////////////////////////////////////////////
// Constants 
///////////////////////////////////////////////////////////////////////////////

// Size of cell cache for aggregation.
// (Since there are 254 possible hosts in class C network subnet the last o
// octed -1 of the cell's IP can be used as cache index (1-1=0 to 254-1=253).)
const uint8_t CELL_CACHE_SIZE = 254;

// Size of message buffer in bytes.
const uint8_t MESSAGE_BUFFER_SIZE = 10;

///////////////////////////////////////////////////////////////////////////////
// Global variables
///////////////////////////////////////////////////////////////////////////////

WiFiUDP udp;

// Cached cell information.
// The last octet -1 of the cell's IP is used as index (1-1=0 to 254-1=253).
cellCacheEntity_t cellCache[CELL_CACHE_SIZE];

// Cached aggregated pack state.
aggregatedPackState_t packCache = {
    0, // voltageMin
    0, // voltageMax
    0, // voltageMean
    0, // numberOfCells
};

// State of the cell.
cellState_t state = {
    0,                        // voltage
    false,                    // isBalancing
    BALANCING_PASSIVE_AUTO,   // balancingMode
};

// Timestamp of last cell state message broadcast
uint32_t lastStateBroadcast = 0;

///////////////////////////////////////////////////////////////////////////////
// Functions
///////////////////////////////////////////////////////////////////////////////

// Inits GPIO of ESP8266.
void initGPIO() {
    // Init PIN_ADC_GROUND as output and set it to low in
    // order to use it as ground for ADC voltage divider.
    pinMode(PIN_ADC_GROUND, OUTPUT);
    digitalWrite(PIN_ADC_GROUND, LOW);

    // Init PIN_MOSFET as output and initially set it to low.
    pinMode(PIN_MOSFET, OUTPUT);
    digitalWrite(PIN_MOSFET, LOW);
}

// Connects to WiFi using the configured WIFI_SSID and WIFI_PASSPHRASE.
void initNetwork() {
    Serial.print("Connecting to WiFi \"");
    Serial.print(WIFI_SSID);
    Serial.print("\"");

    WiFi.begin(WIFI_SSID, WIFI_PASSPHRASE);
    while (WiFi.status() != WL_CONNECTED) {
        Serial.print(".");
        delay(500);
    }

    Serial.println();
    Serial.print("IP address: ");
    Serial.println(WiFi.localIP());
    Serial.println();
}

// Binds UDP socket to PORT
void initSocket() {
    Serial.println("Creating UDP socket...");

    udp.begin(PORT);

    Serial.print("Local port: ");
    Serial.println(udp.localPort());
    Serial.println();
}

// Clears cell cache.
void clearCellCache() {
    for(uint8_t i = 0; i < CELL_CACHE_SIZE; ++i) {
        cellCache[i].lastUpdated = 0;
    }
}

void setup() {
    Serial.begin(115200);
    Serial.println();

    initGPIO();
    initNetwork();
    initSocket();

    clearCellCache();
}

// Updates cached cell state.
void updateCachedCellState(uint8_t idx, const cellState_t &cell) {
    cellCache[idx].voltage = cell.voltage;
    cellCache[idx].lastUpdated = millis();
}

// Updates cached cell state.
void updateCachedCellState(const IPAddress &cellIP, const cellState_t &cell) {
    updateCachedCellState(cellIP[3] - 1, cell);
}

// Gets cached cell state.
// Returns TRUE if cached cell state was updated within CELL_CACHE_MEMORY,
// otherwise FALSE is returned and cell is undefined.
bool getCachedCellState(uint8_t idx, cellCacheEntity_t **cell) {
    if(cellCache[idx].lastUpdated == 0) {
        return false;
    } else if(cellCache[idx].lastUpdated + CELL_CACHE_MEMORY < millis()) {
        cellCache[idx].lastUpdated = 0;
        return false;
    } else {
        *cell = &cellCache[idx];
        return true;
    }
}

// Updates cached pack state.
void updateCachedPackState(const aggregatedPackState_t &pack) {
    memcpy(&packCache, &pack, sizeof(aggregatedPackState_t));
}

// Aggregates the currently cached cell states to a pack state.
aggregatedPackState_t aggregatePackState() {
    aggregatedPackState_t pack { 0, 0, 0, 0 };
    cellCacheEntity_t *cell;
    uint32_t sum = 0;
    for(uint8_t i = 0; i < CELL_CACHE_SIZE; ++i) {
        if(getCachedCellState(i, &cell)) {
            if(pack.voltageMin == 0 || cell->voltage < pack.voltageMin) {
                pack.voltageMin = cell->voltage;
            }
            if(cell->voltage > pack.voltageMax) {
                pack.voltageMax = cell->voltage;
            }
            sum += cell->voltage;
            pack.numberOfCells++;
        }
    }
    if(pack.numberOfCells != 0) {
        pack.voltageMean = sum / pack.numberOfCells;
    }
    return pack;
}

// Measures cell voltage in mV.
uint16_t measureCellVoltage() {
    uint32_t val = analogRead(PIN_ADC_BATTERY);
    return (val * 7625) / 1408;
}

// Controls balancing circuit according to cell's balancing mode.
// If suppress is TRUE the balancing will be turned off. This can be used to
// suppress the balancing before a measurement.
void doBalancing(boolean suppress) {
    if(state.balancingMode == BALANCING_PASSIVE_AUTO) {
        state.isBalancing = (state.voltage > packCache.voltageMin);
    }
    digitalWrite(PIN_MOSFET, (state.isBalancing && !suppress) ? HIGH : LOW);
}

// Sends len bytes of buffer to ip:port.
void send(const uint8_t* buffer, size_t len, IPAddress ip, uint16_t port) {
    udp.beginPacket(ip, port);
    udp.write(buffer, len);
    udp.endPacket();
}

// Writes cell state message (MESSAGE_STATE) to the given buffer.
// Returns len of message in buffer. If the buffer is to small -1 is returned.
ssize_t writeStateMessage(uint8_t *buffer, size_t len, const cellState_t &cell) {
    if(len < 5) return -1;
    buffer[0] = MESSAGE_STATE_UPDATE;
    buffer[1] = state.voltage >> 8 & 0xff;
    buffer[2] = state.voltage & 0xff;
    buffer[3] = state.isBalancing ? 1 : 0;
    buffer[4] = state.balancingMode;
    return 5;
}

// Handles state update message.
// Updates cached cell state of the sender cell.
void handleStateUpdateMessage(const uint8_t* buffer, size_t len, IPAddress remoteIP, uint16_t remotePort) {
    if(len < 5) return;
    cellState_t cell;
    cell.voltage = (buffer[1] << 8) | (buffer[2] & 0xff);
    cell.isBalancing = buffer[3] == 1;
    cell.balancingMode = (balancingMode_t)buffer[4];

    Serial.print("STATE UPDATE MSG (");
    Serial.print(remoteIP.toString());
    Serial.print("): ");
    Serial.print(cell.voltage);
    Serial.print(" ");
    Serial.print(cell.isBalancing);
    Serial.print(" ");
    Serial.println(cell.balancingMode);

    updateCachedCellState(remoteIP, cell);
}

// Handles balancing control message.
// Updates local cell state with new balancing settings.
void handleBalancingControlMessage(const uint8_t* buffer, size_t len, IPAddress remoteIP, uint16_t remotePort) {
    if(len < 3) return;
    state.balancingMode = (balancingMode_t)buffer[1];

    if(state.balancingMode == BALANCING_PASSIVE_MANUAL) {
        state.isBalancing = buffer[2] == 1;
    }

    Serial.print("BALANCING CONTROL MSG (");
    Serial.print(remoteIP.toString());
    Serial.print("): ");
    Serial.print(state.balancingMode);
    if(state.balancingMode == BALANCING_PASSIVE_MANUAL) {
        Serial.print(" ");
        Serial.print(state.isBalancing);
    }
    Serial.println();
}

void handleMessage(const uint8_t* buffer, size_t len, IPAddress remoteIP, uint16_t remotePort) {
    if(len < 1) return;
    messageType_t type = (messageType_t)buffer[0];
    switch(type) {
        case MESSAGE_STATE_UPDATE: handleStateUpdateMessage(buffer, len, remoteIP, remotePort); break;
        case MESSAGE_BALANCING_CONTROL: handleBalancingControlMessage(buffer, len, remoteIP, remotePort); break;
    }
}

void loop() {
    static uint8_t messageBuffer[MESSAGE_BUFFER_SIZE];
    size_t len;
    IPAddress ip;

    uint32_t now = millis();

    // read and handle messages
    while(udp.parsePacket()) {
        int len = udp.read(messageBuffer, MESSAGE_BUFFER_SIZE);
        handleMessage(messageBuffer, len, udp.remoteIP(), udp.remotePort());
    }

    // control balancing (suppress balancing if we will mesaure within the next 50 ms)
    boolean suppress = lastStateBroadcast + STATE_BROADCAST_INTERVAL <= now + 50;
    doBalancing(suppress);

    if(lastStateBroadcast + STATE_BROADCAST_INTERVAL <= now) {
        // measure cell state
        state.voltage = measureCellVoltage();

        // update own entry in cell cache
        updateCachedCellState(WiFi.localIP(), state);

        // calculate pack state from cell cache and update pack cache
        updateCachedPackState(aggregatePackState());

        // broadcast cell state
        len = writeStateMessage(messageBuffer, MESSAGE_BUFFER_SIZE, state);
        ip.fromString("255.255.255.255");
        send(messageBuffer, len, ip, PORT);

        lastStateBroadcast = now;
    }

    delay(10);
}
