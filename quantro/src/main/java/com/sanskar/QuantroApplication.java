package com.sanskar;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class QuantroApplication {

    public static void main(String[] args) {

        // Load .env file safely
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();

        // Database
        setEnv(dotenv, "DB_URL");
        setEnv(dotenv, "DB_USERNAME");
        setEnv(dotenv, "DB_PASSWORD");

        // JWT
        setEnv(dotenv, "JWT_SECRET");

        // Email
        setEnv(dotenv, "MAIL_USERNAME");
        setEnv(dotenv, "MAIL_PASSWORD");

        // Stripe
        setEnv(dotenv, "STRIPE_API_KEY");

        // Razorpay
        setEnv(dotenv, "RAZORPAY_API_KEY");
        setEnv(dotenv, "RAZORPAY_API_SECRET");

        // CoinGecko
        setEnv(dotenv, "COINGECKO_API_KEY");

        // Gemini
        setEnv(dotenv, "GEMINI_API_KEY");

        // Google OAuth
        setEnv(dotenv, "GOOGLE_CLIENT_ID");
        setEnv(dotenv, "GOOGLE_CLIENT_SECRET");

        SpringApplication.run(QuantroApplication.class, args);
    }

    // Helper method to avoid NullPointerException
    private static void setEnv(Dotenv dotenv, String key) {
        String value = dotenv.get(key);

        if (value != null && !value.isBlank()) {
            System.setProperty(key, value);
        }
    }
}
