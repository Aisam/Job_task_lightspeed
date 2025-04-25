package IP_Addr_Counter;

import java.io.*;
import java.util.BitSet;

public class IpAddrCounter {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java IpAddrCounter <input_file>");
            return;
        }

        String filename = args[0];
        
        // Number of possible ipv4 addresses = 2^32 = 4294967296 
        // Java BitSet can represent bits with indices from 0 to 2147483647
        // Thus, other half of ipv4 addresses will be stored in ipSetNeg
        BitSet ipSetPos = new BitSet(Integer.MAX_VALUE); 
        BitSet ipSetNeg = new BitSet(Integer.MAX_VALUE);
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            long lineCount = 0;

            while ((line = reader.readLine()) != null) {
            	// convert ipv4 addresses from string representation to int
                int ipInt = ipToInt(line.trim());
                
                // turn appropriate ipv4 address bit in BitSet
                if (ipInt == Integer.MIN_VALUE) {
                	ipSetNeg.set(0);
                } else if(ipInt < 0) {
                	ipInt = -ipInt;
                	ipSetNeg.set(ipInt);
                } else {
                	ipSetPos.set(ipInt);
                }
                
                lineCount++;
                
                if (lineCount % 10 == 0) {
                    System.out.printf("Processed %,d lines...\n", lineCount);
                }
            }
            Long total = (long) (ipSetPos.cardinality() + ipSetNeg.cardinality());
            System.out.println("Total unique IP addresses: " + total);

        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }

    /**
     * Converts an IPv4 address string to a 32-bit integer.
     */
    private static int ipToInt(String ipAddress) {
        var ipArray = ipAddress.split("\\.");
        int ipInt = 0;
        for (int i = 0; i < ipArray.length; i++) {
            int power = ipArray.length - 1 - i;
            int ip = Integer.parseInt(ipArray[i]);
            // add up bits from each part, each consisting 8 bits, 2^8=256 
            ipInt += ip * Math.pow(256, power);
        }
        return ipInt;
    }
}