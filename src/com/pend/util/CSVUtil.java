package com.pend.util;

import com.pend.model.Transaction;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Lightweight CSV import/export. Simple escaping (commas replaced).
 */
public class CSVUtil {

    public static void exportToCSV(List<Transaction> list, java.io.File file) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            pw.println("date,amount,category,description,currency");
            for (Transaction t : list) {
                pw.printf("%s,%.2f,%s,%s,%s%n",
                        t.getDate().toString(),
                        t.getAmount(),
                        escape(t.getCategory()),
                        escape(t.getDescription()),
                        t.getCurrency());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Transaction> importFromCSV(java.io.File file) {
        List<Transaction> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String header = br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] cols = parseCSVLine(line);
                if (cols.length >= 4) {
                    LocalDate date = LocalDate.parse(cols[0]);
                    double amt = Double.parseDouble(cols[1]);
                    String cat = cols[2];
                    String desc = cols[3];
                    String cur = cols.length > 4 ? cols[4] : "USD";
                    list.add(new Transaction(date, amt, cat, desc, cur));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace(",", " ");
    }

    private static String[] parseCSVLine(String line) {
        // naive split (sufficient for exported files from this app)
        return line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
    }
}
