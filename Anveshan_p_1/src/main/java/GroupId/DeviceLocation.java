package GroupId;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class DeviceLocation {
    public static void main(String[] args) {
        try {
            // Get the network interfaces
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                // Exclude loopback and inactive interfaces
                if (iface.isLoopback() || !iface.isUp()) {
                    continue;
                }

                // Get the addresses associated with the interface
                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    // Check if the address is an IPv4 address and not a link-local or loopback address
                    if (address.getAddress().length == 4 && !address.isLinkLocalAddress() && !address.isLoopbackAddress()) {
                        System.out.println("Device location: " + address.getHostAddress());
                        return;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Device location: Unknown");
    }
}