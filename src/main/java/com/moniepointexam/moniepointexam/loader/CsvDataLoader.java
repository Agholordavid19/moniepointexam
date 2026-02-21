package com.moniepointexam.moniepointexam.loader;

import com.moniepointexam.moniepointexam.model.enums.Product;
import com.moniepointexam.moniepointexam.model.enums.Status;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.io.*;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Component
public class CsvDataLoader implements CommandLineRunner {

    private final DataSource dataSource;

    public CsvDataLoader(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private static final String DATA_DIR = "data/sample_data";

    private static final String INSERT_SQL =
            "INSERT INTO events (event_id, merchant_id, event_timestamp, product, event_type, amount, status, channel, region, merchant_tier) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON CONFLICT (event_id) DO NOTHING";

    @Override
    public void run(String... args) throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            ResultSet rs = conn.createStatement().executeQuery("SELECT COUNT(*) FROM events");
            rs.next();
            if (rs.getLong(1) > 0) {
                log.info("Database already populated, skipping CSV load.");
                return;
            }
        }

        File folder = new File(DATA_DIR);
        File[] csvFiles = folder.listFiles((dir, name) -> name.endsWith(".csv"));

        if (csvFiles == null || csvFiles.length == 0) {
            log.warn("No CSV files found in {}", DATA_DIR);
            return;
        }

        int totalLoaded = 0;
        for (File file : csvFiles) {
            int loaded = loadFile(file);
            log.info("Loaded {} records from {}", loaded, file.getName());
            totalLoaded += loaded;
        }

        log.info("Total records loaded: {}", totalLoaded);
    }

    private int loadFile(File file) {
        int count = 0;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_SQL);
             BufferedReader reader = new BufferedReader(new FileReader(file))) {

            conn.setAutoCommit(false);

            String header = reader.readLine();
            if (header == null) return 0;

            String line;
            int batchCount = 0;

            while ((line = reader.readLine()) != null) {
                try {
                    String[] cols = line.split(",", -1);
                    if (cols.length < 10) continue;

                    String timestampStr = cols[2].trim();
                    if (timestampStr.equals("NOT-A-DATE") || timestampStr.isBlank()) continue;

                    ps.setObject(1, UUID.fromString(cols[0].trim()));
                    ps.setString(2, cols[1].trim());
                    ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.parse(timestampStr)));
                    ps.setString(4, cols[3].trim().toUpperCase());
                    ps.setString(5, cols[4].trim());
                    ps.setBigDecimal(6, new BigDecimal(cols[5].trim()));
                    ps.setString(7, cols[6].trim().toUpperCase());
                    ps.setString(8, cols[7].trim());
                    ps.setString(9, cols[8].trim());
                    ps.setString(10, cols[9].trim());

                    ps.addBatch();
                    batchCount++;
                    count++;

                    if (batchCount == 500) {
                        ps.executeBatch();
                        conn.commit();
                        batchCount = 0;
                    }

                } catch (Exception e) {
                    log.warn("Skipping malformed row in {}: {}", file.getName(), e.getMessage());
                }
            }

            if (batchCount > 0) {
                ps.executeBatch();
                conn.commit();
            }

        } catch (Exception e) {
            log.error("Failed to load file {}: {}", file.getName(), e.getMessage());
        }

        return count;
    }
}