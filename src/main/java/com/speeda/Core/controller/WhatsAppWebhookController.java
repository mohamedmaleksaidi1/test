package com.speeda.Core.controller;

import com.speeda.Core.model.Activity;
import com.speeda.Core.model.AuthToken;
import com.speeda.Core.model.Enum.UserStatus;
import com.speeda.Core.model.Preference;
import com.speeda.Core.model.User;
import com.speeda.Core.repository.ActivityRepository;
import com.speeda.Core.repository.AuthTokenRepository;
import com.speeda.Core.repository.PreferenceRepository;
import com.speeda.Core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.*;
@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
public class WhatsAppWebhookController {
    private static final String VERIFY_TOKEN = "whatsappWebhookToken2024";
    private static final String N8N_WEBHOOK_URL = "https://n8n.speeda.ai/webhook-test/e86f9292-10ec-4025-87f6-e46f9dcd9cce";
    private final RestTemplate restTemplate = new RestTemplate();
    private final UserRepository userRepository;
    private final AuthTokenRepository authTokenRepository;
    private final ActivityRepository activityRepository;
    private final PreferenceRepository preferenceRepository;
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

            boolean tokenValide = false;
            boolean activityExist = false;
            boolean preferenceExist = false;
            String accessToken = null;

            Optional<User> userOpt = userRepository.findByPhoneNumber(phoneNumber);

            if (userOpt.isEmpty()) {
                User newUser = User.builder()
                        .phoneNumber(phoneNumber)
                        .status(UserStatus.INACTIF)
                        .build();
                userRepository.save(newUser);
                System.out.println("👤 Nouvel utilisateur enregistré avec le numéro : " + phoneNumber);
                userOpt = Optional.of(newUser);
            }

            User user = userOpt.get();

            Optional<AuthToken> lastToken = authTokenRepository.findByUser(user).stream()
                    .sorted(Comparator.comparing(AuthToken::getExpiryDate).reversed())
                    .findFirst();

            if (lastToken.isPresent()) {
                tokenValide = lastToken.get().getExpiryDate().isAfter(Instant.now());
                accessToken = lastToken.get().getAccessToken();  
            }

            activityExist = activityRepository.findByUser(user).isPresent();
            preferenceExist = preferenceRepository.findByUser(user).isPresent();
            System.out.println("📥 Message           : " + message);
            System.out.println("📞 Numéro            : " + phoneNumber);
            System.out.println("🔐 Token valide      : " + tokenValide);
            System.out.println("🔑 Access Token      : " + accessToken);
            System.out.println("📊 Activité existe   : " + activityExist);
            System.out.println("🎯 Préférence existe : " + preferenceExist);
            System.out.println("🏷️ Statut utilisateur : " + user.getStatus().name());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> toSend = new HashMap<>();
            toSend.put("phone", phoneNumber);
            toSend.put("message", message);
            toSend.put("token_valide", tokenValide);
            toSend.put("access_token", accessToken); // Ajouté dans le JSON envoyé
            toSend.put("activity_exist", activityExist);
            toSend.put("preference_exist", preferenceExist);
            toSend.put("status", user.getStatus().name());

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
