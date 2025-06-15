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

    private static final String VERIFY_TOKEN = "whatsappWebhookToken2024";
    private static final String ACCESS_TOKEN = "EAAkTQAndy9EBOxzVcmfNkLUjxRsTChjBinuPyq4ZA9LsvyZBl1muAAd1fwBnCcnF8KjZAjsaTJl4alAjrpAs1gZAgKYlAyrKN6HEx6SGyhZBH6adR0FOMz8oYSZB49teZC76TXqH13lI2T9abuXMH7ZB0Jd9WfIfHQrAoul63oqUjTcZBay3zZC2giPZBDuuZAtAPzNlxgZA6cDbTJVNW5zYJesgAqKFoZB52EtElcArMZD";
    private static final String PHONE_NUMBER_ID = "480664495133441";
    private static final String N8N_WEBHOOK_URL = "https://n8n.speeda.ai/webhook-test/2c67be06-d34f-4a0b-b16e-a6938a1fa77f";

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/whatsapp")
    public ResponseEntity<String> verifyWebhook(
            @RequestParam("hub.mode") String mode,
            @RequestParam("hub.challenge") String challenge,
            @RequestParam("hub.verify_token") String token) {
        return VERIFY_TOKEN.equals(token)
                ? ResponseEntity.ok(challenge)
                : ResponseEntity.status(403).body("Verify token incorrect !");
    }

    @PostMapping("/whatsapp")
    public ResponseEntity<?> receiveWhatsAppEvent(@RequestBody Map<String, Object> payload) {
        try {
            String phoneNumber = extractPhoneNumber(payload);
            String message = extractMessageBody(payload);

            if (phoneNumber == null || message == null) {
                return ResponseEntity.ok(Map.of("status", "fail", "message", "message ou numéro manquant"));
            }

            System.out.println("📥 Message reçu : " + message + " de " + phoneNumber);

            // 1. Appeler le webhook n8n et attendre la réponse AI
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> toSend = Map.of(
                    "phone", phoneNumber,
                    "message", message
            );

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(toSend, headers);

            ResponseEntity<Map> aiResponse = restTemplate.postForEntity(N8N_WEBHOOK_URL, entity, Map.class);

            if (!aiResponse.getStatusCode().is2xxSuccessful() || aiResponse.getBody() == null) {
                return ResponseEntity.status(502).body(Map.of("status", "fail", "message", "Erreur de réponse n8n"));
            }

            String aiReply = (String) aiResponse.getBody().get("answer"); // adapte si ton nœud s'appelle "reply", "message", etc.

            System.out.println("🧠 Réponse AI : " + aiReply);

            // 2. Envoyer la réponse au client via l'API WhatsApp
            sendWhatsAppMessage(phoneNumber, aiReply);

            return ResponseEntity.ok(Map.of("status", "success", "message", "Message AI envoyé à l’utilisateur"));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("status", "error", "message", "Erreur serveur"));
        }
    }

    private void sendWhatsAppMessage(String phone, String message) {
        String url = "https://graph.facebook.com/v18.0/" + PHONE_NUMBER_ID + "/messages";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(ACCESS_TOKEN);

        Map<String, Object> body = Map.of(
                "messaging_product", "whatsapp",
                "to", phone,
                "type", "text",
                "text", Map.of("body", message)
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        restTemplate.postForEntity(url, request, String.class);
    }

    private String extractPhoneNumber(Map<String, Object> payload) {
        try {
            List<?> entry = (List<?>) payload.get("entry");
            Map<?, ?> entryObj = (Map<?, ?>) entry.get(0);
            List<?> changes = (List<?>) entryObj.get("changes");
            Map<?, ?> changeObj = (Map<?, ?>) changes.get(0);
            Map<?, ?> value = (Map<?, ?>) changeObj.get("value");
            List<?> messages = (List<?>) value.get("messages");
            Map<?, ?> message = (Map<?, ?>) messages.get(0);
            return (String) message.get("from");
        } catch (Exception e) {
            return null;
        }
    }

    private String extractMessageBody(Map<String, Object> payload) {
        try {
            List<?> entry = (List<?>) payload.get("entry");
            Map<?, ?> entryObj = (Map<?, ?>) entry.get(0);
            List<?> changes = (List<?>) entryObj.get("changes");
            Map<?, ?> changeObj = (Map<?, ?>) changes.get(0);
            Map<?, ?> value = (Map<?, ?>) changeObj.get("value");
            List<?> messages = (List<?>) value.get("messages");
            Map<?, ?> message = (Map<?, ?>) messages.get(0);
            Map<?, ?> text = (Map<?, ?>) message.get("text");
            return (String) text.get("body");
        } catch (Exception e) {
            return null;
        }
    }
}
