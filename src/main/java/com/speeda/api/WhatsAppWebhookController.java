package com.speeda.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;


import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@RestController
@RequestMapping("/webhook")
public class WhatsAppWebhookController {

    // 🔐 Infos Meta statiques (à rendre dynamiques plus tard)
    private static final String CLIENT_ID = "2554440554761169";
    private static final String CLIENT_SECRET = "8d4c79fcd07115d692e8f8bc6de79f77";
    private static final String ACCESS_TOKEN = "EAAkTQAndy9EBO7AokLedHV1oCUvZBzuMi3YVfALEByFhsblwQweRCeY1AuStQxh0l0QkLOg0R7rweRiPuYyizeNC7cTdlCJgdgygj6DVsTZCA6o26fwclhne0UV0kR57fJ6crYDKNQs8YGn078m20uSlbd90BZADZAq2K3RQa7HvZA6ZBZBBNJtnzbx9m99Db7raOqoPRm0QxLJeknEIaGLZAwxWMN5Cx45GreNd";
    private static final String PHONE_NUMBER_ID = "480664495133441";
    private static final String VERIFY_TOKEN = "whatsappWebhookToken2024";
    private static final String N8N_WEBHOOK_URL = "https://n8n.speeda.ai/webhook-test/2c67be06-d34f-4a0b-b16e-a6938a1fa77f";

    private final RestTemplate restTemplate = new RestTemplate();

    // ✅ Vérification Meta (GET)
    @GetMapping("/whatsapp")
    public ResponseEntity<String> verifyWebhook(
            @RequestParam("hub.mode") String mode,
            @RequestParam("hub.challenge") String challenge,
            @RequestParam("hub.verify_token") String token) {

        if (VERIFY_TOKEN.equals(token)) {
            return ResponseEntity.ok(challenge);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Verify token incorrect !");
        }
    }

    // ✅ Réception de message WhatsApp (POST)
    @PostMapping("/whatsapp")
    public ResponseEntity<?> receiveWhatsAppEvent(@RequestBody Map<String, Object> payload) {
        try {
            String phoneNumber = extractPhoneNumber(payload);
            String message = extractMessageBody(payload);

            if (phoneNumber == null || message == null) {
                System.out.println("❌ Message ou numéro manquant.");
                return ResponseEntity.ok(Map.of("status", "fail", "message", "message ou numéro manquant"));
            }

            System.out.println("📥 Message reçu : " + message + " de " + phoneNumber);

            // ➤ Construction du corps pour n8n
            Map<String, Object> toSend = Map.of(
                    "phone", phoneNumber,
                    "message", message,
                    "client_id", CLIENT_ID,
                    "client_secret", CLIENT_SECRET,
                    "access_token", ACCESS_TOKEN,
                    "phone_number_id", PHONE_NUMBER_ID
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(toSend, headers);
            restTemplate.postForEntity(N8N_WEBHOOK_URL, request, String.class);

            return ResponseEntity.ok(Map.of("status", "relay sent to n8n"));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("status", "error", "message", "Erreur serveur"));
        }
    }

    // 🔧 Extraction du numéro de téléphone
    private String extractPhoneNumber(Map<String, Object> payload) {
        try {
            List<?> entry = (List<?>) payload.get("entry");
            if (entry == null || entry.isEmpty()) return null;

            Map<?, ?> entryObj = (Map<?, ?>) entry.get(0);
            List<?> changes = (List<?>) entryObj.get("changes");
            if (changes == null || changes.isEmpty()) return null;

            Map<?, ?> changeObj = (Map<?, ?>) changes.get(0);
            Map<?, ?> value = (Map<?, ?>) changeObj.get("value");
            if (value == null) return null;

            List<?> messages = (List<?>) value.get("messages");
            if (messages == null || messages.isEmpty()) return null;

            Map<?, ?> message = (Map<?, ?>) messages.get(0);
            return (String) message.get("from");

        } catch (Exception e) {
            System.err.println("Erreur extractPhoneNumber: " + e.getMessage());
            return null;
        }
    }

    // 🔧 Extraction du message texte
    private String extractMessageBody(Map<String, Object> payload) {
        try {
            List<?> entry = (List<?>) payload.get("entry");
            if (entry == null || entry.isEmpty()) return null;

            Map<?, ?> entryObj = (Map<?, ?>) entry.get(0);
            List<?> changes = (List<?>) entryObj.get("changes");
            if (changes == null || changes.isEmpty()) return null;

            Map<?, ?> changeObj = (Map<?, ?>) changes.get(0);
            Map<?, ?> value = (Map<?, ?>) changeObj.get("value");
            if (value == null) return null;

            List<?> messages = (List<?>) value.get("messages");
            if (messages == null || messages.isEmpty()) return null;

            Map<?, ?> message = (Map<?, ?>) messages.get(0);
            Map<?, ?> text = (Map<?, ?>) message.get("text");
            return (String) text.get("body");

        } catch (Exception e) {
            System.err.println("Erreur extractMessageBody: " + e.getMessage());
            return null;
        }
    }
}

