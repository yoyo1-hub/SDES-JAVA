/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s.des;

/**
 *
 * @author user
 */
import java.util.Scanner;

public class SDES_Menu {

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {

        while (true) {
            System.out.println("\n====== S-DES MENU ======");
            System.out.println("1- Encrypt");
            System.out.println("2- Decrypt");
            System.out.println("3- Exit");
            System.out.print("Choose: ");

            int choice = sc.nextInt();
            sc.nextLine();

            if (choice == 3) {
                System.out.println("Goodbye 👋");
                break;
            }

            System.out.print("Enter 10-bit key: ");
            String key = sc.nextLine();

            if (!isValid(key, 10)) {
                System.out.println("❌ Invalid key!");
                continue;
            }

            System.out.print("Enter 8-bit text: ");
            String text = sc.nextLine();

            if (!isValid(text, 8)) {
                System.out.println("❌ Invalid text!");
                continue;
            }

            String[] keys = generateKeys(key);

            if (choice == 1) {
                String cipher = encrypt(text, keys[0], keys[1]);
                System.out.println("\n--- RESULT ---");
                System.out.println("K1: " + keys[0]);
                System.out.println("K2: " + keys[1]);
                System.out.println("Cipher: " + cipher);
            } 
            else if (choice == 2) {
                String plain = decrypt(text, keys[0], keys[1]);
                System.out.println("\n--- RESULT ---");
                System.out.println("K1: " + keys[0]);
                System.out.println("K2: " + keys[1]);
                System.out.println("Plain: " + plain);
            } 
            else {
                System.out.println("❌ Invalid choice!");
            }
        }
    }

    // ================= VALIDATION =================
    static boolean isValid(String s, int length) {
        if (s.length() != length) return false;
        for (char c : s.toCharArray()) {
            if (c != '0' && c != '1') return false;
        }
        return true;
    }

    // ================= ENCRYPT =================
    static String encrypt(String text, String k1, String k2) {
        String ip = permute(text, IP);
        String r1 = fk(ip, k1);
        String swapped = swap(r1);
        String r2 = fk(swapped, k2);
        return permute(r2, IP_INV);
    }

    // ================= DECRYPT =================
    static String decrypt(String text, String k1, String k2) {
        String ip = permute(text, IP);
        String r1 = fk(ip, k2); // عكس المفاتيح
        String swapped = swap(r1);
        String r2 = fk(swapped, k1);
        return permute(r2, IP_INV);
    }

    // ================= FK =================
    static String fk(String input, String key) {
        String L = input.substring(0,4);
        String R = input.substring(4);
        String f = F(R, key);
        return xor(L, f) + R;
    }

    // ================= F =================
    static String F(String R, String key) {
        String ep = permute(R, EP);
        String x = xor(ep, key);
        String left = x.substring(0,4);
        String right = x.substring(4);
        String s0 = SBOX(left, S0);
        String s1 = SBOX(right, S1);
        return permute(s0 + s1, P4);
    }

    // ================= KEY GEN =================
    static String[] generateKeys(String key) {
        String p10 = permute(key, P10);

        String L = p10.substring(0,5);
        String R = p10.substring(5);

        // LS-1
        L = shift(L,1);
        R = shift(R,1);
        String k1 = permute(L + R, P8);

        // LS-2
        L = shift(L,2);
        R = shift(R,2);
        String k2 = permute(L + R, P8);

        return new String[]{k1, k2};
    }

    // ================= HELPERS =================
    static String swap(String s) {
        return s.substring(4) + s.substring(0,4);
    }

    static String permute(String s, int[] t) {
        StringBuilder out = new StringBuilder();
        for (int i : t)
            out.append(s.charAt(i - 1));
        return out.toString();
    }

    static String shift(String s, int n) {
        return s.substring(n) + s.substring(0,n);
    }

    static String xor(String a, String b) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < a.length(); i++)
            out.append(a.charAt(i) == b.charAt(i) ? "0" : "1");
        return out.toString();
    }

    static String SBOX(String bits, int[][] box) {
        int row = Integer.parseInt("" + bits.charAt(0) + bits.charAt(3), 2);
        int col = Integer.parseInt("" + bits.charAt(1) + bits.charAt(2), 2);
        int val = box[row][col];
        return String.format("%2s", Integer.toBinaryString(val)).replace(' ', '0');
    }

    // ================= TABLES =================
    static int[] P10 = {3,5,2,7,4,10,1,9,8,6};
    static int[] P8  = {6,3,7,4,8,5,10,9};

    static int[] IP  = {2,6,3,1,4,8,5,7};
    static int[] IP_INV = {4,1,3,5,7,2,8,6};

    static int[] EP  = {4,1,2,3,2,3,4,1};
    static int[] P4  = {2,4,3,1};

    static int[][] S0 = {
        {1,0,3,2},
        {3,2,1,0},
        {0,2,1,3},
        {3,1,3,2}
    };

    static int[][] S1 = {
        {0,1,2,3},
        {2,0,1,3},
        {3,0,1,0},
        {2,1,0,3}
    };
}
