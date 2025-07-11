package com.speeda.Core.controller;

import com.speeda.Core.model.AuthToken;
import com.speeda.Core.model.User;
import com.speeda.Core.repository.AuthTokenRepository;
import com.speeda.Core.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/webhook")
public class WhatsAppWebhookController {

    private static final String VERIFY_TOKEN = "whatsappWebhookToken2024";
    private static final String N8N_WEBHOOK_URL = "https://n8n.speeda.ai/webhook-test/e86f9292-10ec-4025-87f6-e46f9dcd9cce";

    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthTokenRepository authTokenRepository;

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
    public void receiveWhatsAppEvent(@RequestBody Map<String, Object> payload) {
        try {
            String phoneNumber = extractPhoneNumber(payload);
            String message = extractMessageBody(payload);

            if (phoneNumber == null || message == null) {
                System.out.println("❌ Données manquantes");
                return;
            }

            boolean userExist = false;
            boolean tokenValide = false;

            Optional<User> userOpt = userRepository.findByPhoneNumber(phoneNumber);
            if (userOpt.isPresent()) {
                userExist = true;
                User user = userOpt.get();

                // Vérifier le token
                Optional<AuthToken> lastToken = authTokenRepository.findByUser(user).stream()
                        .sorted(Comparator.comparing(AuthToken::getExpiryDate).reversed())
                        .findFirst();

                if (lastToken.isPresent()) {
                    tokenValide = lastToken.get().getExpiryDate().isAfter(Instant.now());
                }
            }

            System.out.println("📥 Message       : " + message);
            System.out.println("📞 Numéro        : " + phoneNumber);
            System.out.println("✅ User existe   : " + userExist);
            System.out.println("🔐 Token valide  : " + tokenValide);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> toSend = Map.of(
                    "phone", phoneNumber,
                    "message", message,
                    "user_exist", userExist,
                    "token_valide", tokenValide
            );

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(toSend, headers);
            restTemplate.postForEntity(N8N_WEBHOOK_URL, entity, Map.class);

        } catch (Exception e) {
            e.printStackTrace();
        }
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
