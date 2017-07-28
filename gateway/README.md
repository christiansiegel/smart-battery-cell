# Setup Gateway

The following setup commands refer to a Raspberry Pi 3 Model B running a fresh install of Raspbian GNU/Linux 8 (jessie).

## Update Raspbian

```
sudo apt-get update
sudo apt-get upgrade
sudo rpi-update
```

## Configure WiFi Access Point

Install the *hostapd* software access point daemon and the dnsmasq network infrastructure pack-
ages with the following command.

```
sudo apt-get install hostapd dnsmasq
```

Stop the DHCP client *dhcpcd* from configuring `wlan0` by adding the following line to the end of the configuration file `/etc/dhcpcd.conf`.

```
denyinterfaces wlan0
```

Edit the `wlan0` part of the inteface configuration file `/etc/network/interfaces` so that it looks like the following in order to configure a static class C network.

```
allow-hotplug wlan0
iface wlan0 inet static
    address 192.168.0.1
    netmask 255.255.255.0
    network 192.168.0.0
    broadcast 192.168.0.255
```

Open the *hostapd* configuration with `/etc/hostapd/hostapd.conf` and change its contents to the following in order to configure the WiFi access point.

```
# This is the name of the WiFi interface we configured above
interface=wlan0

# Use the nl80211 driver with the brcmfmac driver
driver=nl80211
# This is the name of the network
ssid=SmartCellGateway
# Use the 2.4GHz band
hw_mode=g
# Use channel 6
channel=6
# Enable 802.11n
ieee80211n=1
# Enable WMM
wmm_enabled=1
# Enable 40MHz channels with 20ns guard interval
ht_capab=[HT40][SHORT-GI-20][DSSS_CCK-40]
# Accept all MAC addresses
macaddr_acl=0
# Use WPA authentication
auth_algs=1
# Require clients to know the network name
ignore_broadcast_ssid=0
# Use WPA2
wpa=2
# Use a pre-shared key
wpa_key_mgmt=WPA-PSK
# The network passphrase
wpa_passphrase=allcellsarebelongtome
# Use AES, instead of TKIP
rsn_pairwise=CCMP
```

For *hostapd* to load this configuration on startup open the file `/etc/default/hostapd` and replace the line

```
#DAEMON_CONF=""
```

with the following.

```
DAEMON_CONF="/etc/hostapd/hostapd.conf"
```

Configure *dnsmasq* by replacing the contents of `/etc/dnsmasq.conf` with the following.

```
interface=wlan0
listen-address=192.168.0.1
bind-interfaces
domain-needed
bogus-priv
dhcp-range=192.168.0.2,192.168.0.254,24h
```

Reboot the Raspberry Pi.

You should now be able to connect to the WiFi with SSID *SmartCellGateway* using the pre-shared key `allcellsarebelongtome` as configured above.

## Install Apache Ant and Ivy

Install Apache Ant and Ivy by executing the following commands

```
sudo apt-get install ant ivy
sudo ln -s /usr/share/java/ivy.jar /usr/share/ant/lib
```

## Run Gateway Daemon

Copy the gateway daemon sources from this directory to the Raspberry Pi and run the daemon
with the following command.

```
ant daemon
```

